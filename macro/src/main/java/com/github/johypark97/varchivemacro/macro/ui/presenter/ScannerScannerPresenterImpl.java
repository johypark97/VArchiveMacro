package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.desktop.AwtRobotHelper;
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.NativeInputKey;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEventBus;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerScannerStage;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerScannerViewModel;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import io.reactivex.rxjava3.disposables.Disposable;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerScannerPresenterImpl implements ScannerScanner.ScannerScannerPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerScannerPresenterImpl.class);

    private final ScannerScannerStage scannerScannerStage;

    private final GlobalContext globalContext;

    private final StringProperty accountFileText = new SimpleStringProperty();
    private final StringProperty cacheDirectoryText = new SimpleStringProperty();

    private Disposable disposableGlobalEvent;
    private NativeKeyListener nativeKeyListener;
    private ScannerContext scannerContext;

    @MvpView
    public ScannerScanner.ScannerScannerView view;

    public ScannerScannerPresenterImpl(ScannerScannerStage scannerScannerStage,
            GlobalContext globalContext) {
        this.scannerScannerStage = scannerScannerStage;

        this.globalContext = globalContext;
    }

    private void showConfig() {
        ScannerConfig config = globalContext.configService.findScannerConfig();

        accountFileText.set(config.accountFile());
        cacheDirectoryText.set(config.cacheDirectory());

        view.setAutoAnalysis(config.autoAnalysis());
        view.setStartKeyText(config.startKey().toString());
        view.setStopKeyText(config.stopKey().toString());
    }

    private Set<String> getSelectedCategorySet() {
        return view.getSelectedCategoryList().stream().map(x -> x.category.name())
                .collect(Collectors.toSet());
    }

    private synchronized void startScan() {
        try {
            if (scannerContext == null) {
                scannerContext = ContextManager.INSTANCE.createScannerContext();
            }
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

        Set<String> selectedCategorySet = getSelectedCategorySet();
        Task<Void> task = scannerContext.collectionScanTaskService.createTask(selectedCategorySet);
        if (task == null) {
            return;
        }

        task.setOnRunning(event -> view.showProgressBox());

        task.setOnCancelled(ScannerScannerPresenterImpl.this::onStopTask);
        task.setOnFailed(ScannerScannerPresenterImpl.this::onStopTask);
        task.setOnSucceeded(ScannerScannerPresenterImpl.this::onStopTask);

        CompletableFuture.runAsync(task);
    }

    private synchronized void stopScan() {
        if (scannerContext != null) {
            scannerContext.collectionScanTaskService.stopTask();
            scannerContext = null; // NOPMD
        }
    }

    private void registerKeyboardHook() {
        ScannerConfig config = globalContext.configService.findScannerConfig();

        InputKeyCombination startKey = config.startKey();
        InputKeyCombination stopKey = config.stopKey();

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
        Throwable throwable = event.getSource().getException();

        Language language = Language.INSTANCE;

        if (throwable != null) {
            switch (throwable) {
                case NotSupportedResolutionException e -> scannerScannerStage.showError(
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

        scannerScannerStage.showInformation(
                Language.INSTANCE.getString("scanner.scanner.dialog.taskDone"));

        Platform.runLater(scannerScannerStage::stopStage);
        Platform.runLater(() -> GlobalEventBus.INSTANCE.fire(GlobalEvent.SCANNER_SCAN_DONE));
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
            default:
        }
    }

    @Override
    public void startView() {
        disposableGlobalEvent = GlobalEventBus.INSTANCE.subscribe(this::onGlobalEvent);

        view.bindAccountFileText(accountFileText);
        view.bindCacheDirectoryText(cacheDirectoryText);

        view.setCategoryList(globalContext.songService.findAllCategory().stream()
                .map(ScannerScannerViewModel.CategoryData::new).toList());
        view.setSelectedCategory(
                globalContext.configService.findScannerConfig().selectedCategory());

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

        ScannerConfig.Builder builder = globalContext.configService.findScannerConfig().toBuilder();
        builder.selectedCategory = getSelectedCategorySet();
        globalContext.configService.saveScannerConfig(builder.build());

        return true;
    }

    @Override
    public void checkDisplayAndResolution() {
        // TODO: Refactoring required.

        Language language = Language.INSTANCE;

        BufferedImage bufferedImage;
        try {
            bufferedImage = AwtRobotHelper.captureScreenshot(new Robot());
            Dimension dimension =
                    new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());
            CollectionAreaFactory.create(dimension);
        } catch (NotSupportedResolutionException e) {
            scannerScannerStage.showWarning(language.getFormatString(
                    "scanner.scanner.dialog.exception.notSupportedResolution", e.getMessage()));
            return;
        } catch (Exception e) {
            String message = "Unexpected exception";
            scannerScannerStage.showError(message, e);
            return;
        }

        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
        view.setCheckerImageViewImage(image);

        scannerScannerStage.showInformation(
                language.getFormatString("scanner.scanner.dialog.checkerOkHeader"),
                language.getFormatString("scanner.scanner.dialog.checkerOkMessage"));
    }
}
