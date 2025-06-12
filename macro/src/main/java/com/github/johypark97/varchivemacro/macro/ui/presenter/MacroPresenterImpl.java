package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.application.event.GlobalEvent;
import com.github.johypark97.varchivemacro.macro.application.event.GlobalEventBus;
import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroProgress;
import com.github.johypark97.varchivemacro.macro.application.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.application.utility.NativeInputKey;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.concurrent.CompletableFuture;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenterImpl implements Macro.MacroPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenterImpl.class);

    private final ConfigRepository configRepository;

    private final MacroService macroService;

    private final HomeStage homeStage;

    private Disposable disposableGlobalEvent;
    private NativeKeyListener nativeKeyListener;

    @MvpView
    public Macro.MacroView view;

    public MacroPresenterImpl(HomeStage homeStage, ConfigRepository configRepository,
            MacroService macroService) {
        this.configRepository = configRepository;
        this.macroService = macroService;

        this.homeStage = homeStage;
    }

    private void showConfig() {
        MacroConfig config = configRepository.findMacroConfig();

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
        MacroConfig config = configRepository.findMacroConfig();

        InputKeyCombination startUpKey = config.startUpKey();
        InputKeyCombination startDownKey = config.startDownKey();
        InputKeyCombination stopKey = config.stopKey();

        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (nativeInputKey.isInteroperable() && nativeInputKey.isEqual(stopKey)) {
                    macroService.stopMacroTask();
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
                    task = macroService.createMacroTask(MacroDirection.UP);
                } else if (nativeInputKey.isEqual(startDownKey)) {
                    task = macroService.createMacroTask(MacroDirection.DOWN);
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

    private void onGlobalEvent(GlobalEvent event) {
        switch (event) {
            case SETTING_UPDATED:
                showConfig();
                break;
            case SETTING_WINDOW_OPENED:
                unregisterKeyboardHook();
                break;
            case SETTING_WINDOW_CLOSED:
                registerKeyboardHook();
                break;
        }
    }

    @Override
    public void startView() {
        disposableGlobalEvent = GlobalEventBus.INSTANCE.subscribe(this::onGlobalEvent);

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
        MacroConfig config = configRepository.findMacroConfig();

        MacroConfig.Builder builder = MacroConfig.Builder.from(config);
        builder.count = value;

        configRepository.saveMacroConfig(builder.build());
    }

    @Override
    public void decreaseCount10() {
        view.setCount(configRepository.findMacroConfig().count() - 10);
    }

    @Override
    public void decreaseCount1() {
        view.setCount(configRepository.findMacroConfig().count() - 1);
    }

    @Override
    public void increaseCount1() {
        view.setCount(configRepository.findMacroConfig().count() + 1);
    }

    @Override
    public void increaseCount10() {
        view.setCount(configRepository.findMacroConfig().count() + 10);
    }

    @Override
    public void showHome() {
        homeStage.changeCenterView_modeSelector();
    }
}
