package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.NativeInputKey;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.event.ScannerScanDoneUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingUpdatedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingWindowClosedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingWindowOpenedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEventBus;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerScanner;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerScannerViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerScannerStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.reactivex.rxjava3.disposables.Disposable;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerScannerPresenterImpl implements ScannerScanner.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerScannerPresenterImpl.class);

    private final ScannerScannerStage scannerScannerStage;

    private final GlobalContext globalContext;

    private final AtomicBoolean taskRunning = new AtomicBoolean();
    private final StringProperty accountFileText = new SimpleStringProperty();
    private final StringProperty cacheDirectoryText = new SimpleStringProperty();

    private Disposable disposableGlobalEvent;
    private NativeKeyListener nativeKeyListener;
    private ScannerContext scannerContext;

    @MvpView
    public ScannerScanner.View view;

    public ScannerScannerPresenterImpl(ScannerScannerStage scannerScannerStage,
            GlobalContext globalContext) {
        this.scannerScannerStage = scannerScannerStage;

        this.globalContext = globalContext;
    }

    private void showConfig() {
        ScannerConfig config =
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().scannerConfig();

        accountFileText.set(config.accountFile().value());
        cacheDirectoryText.set(config.cacheDirectory().value());

        view.setAutoAnalysis(config.autoAnalysis().value());
        view.setStartKeyText(config.startKey().value().toString());
        view.setStopKeyText(config.stopKey().value().toString());
    }

    private Set<String> getSelectedCategorySet() {
        return view.getSelectedCategoryList().stream().map(x -> x.category.name())
                .collect(Collectors.toSet());
    }

    private synchronized void startScan() {
        if (taskRunning.get()) {
            return;
        }

        Set<String> selectedCategorySet = getSelectedCategorySet();
        if (selectedCategorySet.isEmpty()) {
            scannerScannerStage.showWarning(
                    Language.INSTANCE.getString("scanner.scanner.dialog.categoryNotSelected"));
            return;
        }

        try {
            boolean debug = AppConfigManager.INSTANCE.getAppConfigService().getConfig().debug()
                    && view.getDebugCheckBoxValue();
            scannerContext =
                    ContextManager.INSTANCE.createScannerContext(selectedCategorySet, debug);
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Collection task initialization exception.");
            scannerScannerStage.showError(
                    Language.INSTANCE.getString("scanner.scanner.dialog.taskInitException"),
                    e.toString(), e);
            return;
        } catch (Exception e) {
            String message = "Unexpected collection task initialization exception.";
            LOGGER.atError().setCause(e).log(message);
            scannerScannerStage.showError(message, e.toString(), e);
            return;
        }

        Task<Void> task = scannerContext.scannerScannerService.createTask();
        if (task == null) {
            return;
        }

        task.setOnRunning(event -> view.showProgressBox());

        task.setOnCancelled(ScannerScannerPresenterImpl.this::onStopTask);
        task.setOnFailed(ScannerScannerPresenterImpl.this::onStopTask);
        task.setOnSucceeded(ScannerScannerPresenterImpl.this::onStopTask);

        taskRunning.set(true);
        CompletableFuture.runAsync(task);
    }

    private synchronized void stopScan() {
        if (scannerContext != null) {
            scannerContext.scannerScannerService.stopTask();
        }
    }

    private void registerKeyboardHook() {
        ScannerConfig config =
                AppConfigManager.INSTANCE.getAppConfigService().getConfig().scannerConfig();

        InputKeyCombination startKey = config.startKey().value();
        InputKeyCombination stopKey = config.stopKey().value();

        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (nativeInputKey.isInteroperable() && nativeInputKey.isEqual(stopKey)) {
                    stopScan();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeInputKey nativeInputKey = new NativeInputKey(nativeEvent);
                if (nativeInputKey.isInteroperable() && nativeInputKey.isEqual(startKey)) {
                    startScan();
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

        Language language = Language.INSTANCE;

        if (throwable != null) {
            switch (throwable) {
                case DisplayResolutionException e -> scannerScannerStage.showError(
                        language.getString("scanner.scanner.dialog.taskRunningExceptionHeader"),
                        language.getFormatString(
                                "scanner.scanner.dialog.exception.notSupportedResolution",
                                e.getMessage()), e);
                case DirectoryNotEmptyException e -> scannerScannerStage.showError(
                        language.getString("scanner.scanner.dialog.taskRunningExceptionHeader"),
                        language.getString(
                                "scanner.scanner.dialog.exception.cacheDirectoryNotEmpty"), e);
                default -> {
                    LOGGER.atError().setCause(throwable).log("Collection scan task exception.");
                    scannerScannerStage.showError(
                            language.getString("scanner.scanner.dialog.taskRunningExceptionHeader"),
                            throwable.toString(), throwable);
                }
            }

            view.hideProgressBox();
            return;
        }

        Platform.runLater(scannerScannerStage::stopStage);
        Platform.runLater(
                () -> UiEventBus.INSTANCE.fire(new ScannerScanDoneUiEvent(scannerContext)));
        Platform.runLater(() -> scannerScannerStage.showInformation(
                Language.INSTANCE.getString("scanner.scanner.dialog.taskDone")));
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

        AppConfigService appConfigService = AppConfigManager.INSTANCE.getAppConfigService();

        view.bindAccountFileText(accountFileText);
        view.bindCacheDirectoryText(cacheDirectoryText);

        view.setCategoryList(globalContext.songService.findAllCategory().stream()
                .map(ScannerScannerViewModel.CategoryData::new).toList());
        view.setSelectedCategory(
                appConfigService.getConfig().scannerConfig().selectedCategory().value());

        showConfig();
        registerKeyboardHook();

        if (appConfigService.getConfig().debug()) {
            view.setDebugCheckBoxVisible(true);
        }
    }

    @Override
    public boolean stopView() {
        if (TaskManager.getInstance().isRunningAny()) {
            return false;
        }

        unregisterKeyboardHook();
        disposableGlobalEvent.dispose();

        AppConfigManager.INSTANCE.getAppConfigService().editConfig(
                appConfig -> appConfig.editScannerConfig(
                        scannerConfig -> scannerConfig.setSelectedCategory(
                                getSelectedCategorySet())));

        return true;
    }

    @Override
    public void checkDisplayAndResolution() {
        scannerScannerStage.showScannerTester();
    }
}
