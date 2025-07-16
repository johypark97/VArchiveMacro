package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.NativeInputKey;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.MacroDirection;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.MacroProgress;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.MacroContext;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingUpdatedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingWindowClosedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingWindowOpenedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEventBus;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Macro;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.concurrent.CompletableFuture;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenterImpl implements Macro.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenterImpl.class);

    private final HomeStage homeStage;

    private final GlobalContext globalContext;
    private final MacroContext macroContext;

    private Disposable disposableGlobalEvent;
    private NativeKeyListener nativeKeyListener;

    @MvpView
    public Macro.View view;

    public MacroPresenterImpl(HomeStage homeStage, GlobalContext globalContext,
            MacroContext macroContext) {
        this.homeStage = homeStage;

        this.globalContext = globalContext;
        this.macroContext = macroContext;
    }

    private void showConfig() {
        MacroConfig config = globalContext.configService.findMacroConfig();

        view.setupCountSlider(config.count(), MacroConfig.COUNT_DEFAULT, MacroConfig.COUNT_MIN,
                MacroConfig.COUNT_MAX);

        view.setClientModeText(Language.INSTANCE.getString(switch (config.clientMode()) {
            case AT_ONCE -> "macro.clientMode.atOnce";
            case SEPARATELY -> "macro.clientMode.separately";
        }));
        view.setUploadKeyText(config.uploadKey().toString());

        view.setStartUpKeyText(config.startUpKey().toString());
        view.setStartDownKeyText(config.startDownKey().toString());
        view.setStopKeyText(config.stopKey().toString());
    }

    private void registerKeyboardHook() {
        MacroConfig config = globalContext.configService.findMacroConfig();

        InputKeyCombination startUpKey = config.startUpKey();
        InputKeyCombination startDownKey = config.startDownKey();
        InputKeyCombination stopKey = config.stopKey();

        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (nativeInputKey.isInteroperable() && nativeInputKey.isEqual(stopKey)) {
                    macroContext.macroService.stopMacroTask();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (!nativeInputKey.isInteroperable()) {
                    return;
                }

                Task<MacroProgress> task = null;
                if (nativeInputKey.isEqual(startUpKey)) {
                    task = macroContext.macroService.createMacroTask(MacroDirection.UP);
                } else if (nativeInputKey.isEqual(startDownKey)) {
                    task = macroContext.macroService.createMacroTask(MacroDirection.DOWN);
                }

                if (task == null) {
                    return;
                }

                task.setOnRunning(event -> view.showProgressBox());

                task.setOnCancelled(MacroPresenterImpl.this::onStopTask);
                task.setOnFailed(MacroPresenterImpl.this::onStopTask);
                task.setOnSucceeded(MacroPresenterImpl.this::onStopTask);

                task.valueProperty().addListener(
                        (observable, oldValue, newValue) -> view.setProgress(newValue.value(),
                                newValue.count()));

                CompletableFuture.runAsync(task);
            }
        };

        FxHookWrapper.addKeyListener(nativeKeyListener);
    }

    private void unregisterKeyboardHook() {
        if (nativeKeyListener != null) {
            FxHookWrapper.removeKeyListener(nativeKeyListener);
            nativeKeyListener = null; // NOPMD
        }
    }

    private void onStopTask(WorkerStateEvent event) {
        Throwable throwable = event.getSource().getException();

        if (throwable != null) {
            LOGGER.atError().setCause(throwable).log("Macro service exception.");
            homeStage.showError(Language.INSTANCE.getString("macro.serviceException"), throwable);
        }

        view.hideProgressBox();
    }

    private void onUiEvent(UiEvent uiEvent) {
        switch (uiEvent) {
            case SettingUpdatedUiEvent ignored:
                showConfig();
                break;
            case SettingWindowOpenedUiEvent ignored:
                unregisterKeyboardHook();
                break;
            case SettingWindowClosedUiEvent ignored:
                registerKeyboardHook();
                break;
            default:
        }
    }

    @Override
    public void startView() {
        disposableGlobalEvent = UiEventBus.INSTANCE.subscribe(this::onUiEvent);

        showConfig();
        registerKeyboardHook();
    }

    @Override
    public boolean stopView() {
        if (TaskManager.getInstance().isRunningAny()) {
            return false;
        }

        unregisterKeyboardHook();
        disposableGlobalEvent.dispose();

        return true;
    }

    @Override
    public void updateCount(int value) {
        MacroConfig.Builder builder = globalContext.configService.findMacroConfig().toBuilder();
        builder.count = value;

        globalContext.configService.saveMacroConfig(builder.build());
    }

    @Override
    public void decreaseCount10() {
        view.setCount(globalContext.configService.findMacroConfig().count() - 10);
    }

    @Override
    public void decreaseCount1() {
        view.setCount(globalContext.configService.findMacroConfig().count() - 1);
    }

    @Override
    public void increaseCount1() {
        view.setCount(globalContext.configService.findMacroConfig().count() + 1);
    }

    @Override
    public void increaseCount10() {
        view.setCount(globalContext.configService.findMacroConfig().count() + 10);
    }

    @Override
    public void showHome() {
        homeStage.changeCenterView_modeSelector();
    }
}
