package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerProcessorAnalysisPresenterImpl implements ScannerProcessorAnalysis.Presenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScannerProcessorAnalysisPresenterImpl.class);

    private final ScannerProcessorStage scannerProcessorStage;

    private final ScannerContext scannerContext;

    private final AtomicBoolean analysisCompleted = new AtomicBoolean();

    private Set<Integer> captureEntryIdSetForAnalysis = Set.of();

    @MvpView
    public ScannerProcessorAnalysis.View view;

    public ScannerProcessorAnalysisPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.scannerContext = scannerContext;
    }

    private void setFunctionButtonToStart() {
        view.setFunctionButton(
                Language.INSTANCE.getString("scanner.processor.analysis.startAnalysis"),
                this::startAnalysisTask);
    }

    private void setFunctionButtonToStop() {
        view.setFunctionButton(
                Language.INSTANCE.getString("scanner.processor.analysis.stopAnalysis"),
                this::stopAnalysisTask);
    }

    private void startAnalysisTask() {
        Task<Map<Integer, CaptureAnalysisTaskResult>> task =
                scannerContext.scannerAnalysisService.createTask(captureEntryIdSetForAnalysis);
        if (task == null) {
            return;
        }

        task.progressProperty().addListener(
                (observable, oldValue, newValue) -> view.setProgress(newValue.doubleValue()));

        task.setOnCancelled(this::onTaskCancelled);
        task.setOnFailed(this::onTaskFailed);
        task.setOnRunning(this::onTaskRunning);
        task.setOnSucceeded(this::onTaskSucceeded);

        CompletableFuture.runAsync(task);
    }

    private void stopAnalysisTask() {
        scannerContext.scannerAnalysisService.stopTask();
    }

    private void onTaskRunning(WorkerStateEvent ignored) {
        analysisCompleted.set(false);

        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.analyzing"));
        setFunctionButtonToStop();
    }

    private void onTaskSucceeded(WorkerStateEvent ignored) {
        analysisCompleted.set(true);

        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.analysisDone"));
        setFunctionButtonToStart();

        scannerProcessorStage.showInformation(
                language.getString("scanner.processor.analysis.dialog.taskCompleted"));
    }

    private void onTaskCancelled(WorkerStateEvent ignored) {
        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.analysisCancelled"));
        setFunctionButtonToStart();

        scannerProcessorStage.showInformation(
                language.getString("scanner.processor.analysis.dialog.taskCancelled"));
    }

    private void onTaskFailed(WorkerStateEvent event) {
        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.analysisSuspended"));
        setFunctionButtonToStart();

        Throwable throwable = event.getSource().getException();
        if (throwable instanceof IllegalArgumentException) {
            scannerProcessorStage.showWarning(
                    Language.INSTANCE.getString("scanner.processor.analysis.dialog.queueIsEmpty"));
        } else {
            LOGGER.atError().setCause(throwable).log("Analysis exception.");
            scannerProcessorStage.showError(
                    language.getString("scanner.processor.analysis.dialog.taskException"),
                    throwable);
        }
    }

    @Override
    public void runAnalysis_allCapture() {
        captureEntryIdSetForAnalysis =
                scannerContext.scannerAnalysisService.getAllCaptureEntryIdSet();
        if (captureEntryIdSetForAnalysis.isEmpty()) {
            return;
        }

        if (analysisCompleted.get()) {
            return;
        }

        startAnalysisTask();
    }

    @Override
    public void runAnalysis_selectedSong(List<Integer> selectedSongIdList) {
        captureEntryIdSetForAnalysis =
                scannerContext.scannerAnalysisService.getCaptureEntryIdSetFromSongIdList(
                        selectedSongIdList);

        if (analysisCompleted.get()) {
            return;
        }

        startAnalysisTask();
    }

    @Override
    public void showReviewView() {
        scannerProcessorStage.changeCenterView_review();
    }
}
