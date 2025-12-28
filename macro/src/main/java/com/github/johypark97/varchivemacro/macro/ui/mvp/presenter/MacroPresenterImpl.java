package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigManager;
import com.github.johypark97.varchivemacro.macro.common.config.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.NativeInputKey;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.MacroDirection;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.MacroProgress;
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
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenterImpl implements Macro.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenterImpl.class);

    private final HomeStage homeStage;

    private final MacroContext macroContext;

    private final AtomicBoolean taskRunning = new AtomicBoolean();

    private Disposable disposableGlobalEvent;
    private NativeKeyListener nativeKeyListener;

    @MvpView
    public Macro.View view;

    public MacroPresenterImpl(HomeStage homeStage, MacroContext macroContext) {
        this.homeStage = homeStage;

        this.macroContext = macroContext;
    }

    private void showConfig() {
        MacroConfig config =
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig();

        view.setupCountSlider(config.count().value(), config.count().defaultValue(),
                config.count().min(), config.count().max());

        view.setClientModeText(Language.INSTANCE.getString(switch (config.clientMode().value()) {
            case AT_ONCE -> "macro.clientMode.atOnce";
            case SEPARATELY -> "macro.clientMode.separately";
        }));
        view.setUploadKeyText(config.uploadKey().value().toString());

        view.setStartUpKeyText(config.startUpKey().value().toString());
        view.setStartDownKeyText(config.startDownKey().value().toString());
        view.setStopKeyText(config.stopKey().value().toString());
    }

    private synchronized void startMacro(MacroDirection direction) {
        if (taskRunning.get()) {
            return;
        }

        Task<MacroProgress> task = macroContext.macroService.createMacroTask(direction);
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

        taskRunning.set(true);
        CompletableFuture.runAsync(task);
    }

    private void stopMacro() {
        macroContext.macroService.stopMacroTask();
    }

    private void registerKeyboardHook() {
        MacroConfig config =
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig();

        InputKeyCombination startUpKey = config.startUpKey().value();
        InputKeyCombination startDownKey = config.startDownKey().value();
        InputKeyCombination stopKey = config.stopKey().value();

        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (nativeInputKey.isInteroperable() && nativeInputKey.isEqual(stopKey)) {
                    stopMacro();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (!nativeInputKey.isInteroperable()) {
                    return;
                }

                if (nativeInputKey.isEqual(startUpKey)) {
                    startMacro(MacroDirection.UP);
                } else if (nativeInputKey.isEqual(startDownKey)) {
                    startMacro(MacroDirection.DOWN);
                }
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
        taskRunning.set(false);

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
        AppConfigManager.INSTANCE.getAppConfigService().editConfig(
                appConfig -> appConfig.editMacroConfig(macroConfig -> macroConfig.setCount(value)));
    }

    @Override
    public void decreaseCount10() {
        view.setCount(
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig().count()
                        .value() - 10);
    }

    @Override
    public void decreaseCount1() {
        view.setCount(
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig().count()
                        .value() - 1);
    }

    @Override
    public void increaseCount1() {
        view.setCount(
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig().count()
                        .value() + 1);
    }

    @Override
    public void increaseCount10() {
        view.setCount(
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().macroConfig().count()
                        .value() + 10);
    }

    @Override
    public void showHome() {
        homeStage.changeCenterView_modeSelector();
    }
}
