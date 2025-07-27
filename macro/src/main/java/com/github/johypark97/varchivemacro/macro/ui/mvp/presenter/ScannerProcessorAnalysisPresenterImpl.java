package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerAnalysisViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.awt.Toolkit;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerProcessorAnalysisPresenterImpl implements ScannerProcessorAnalysis.Presenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScannerProcessorAnalysisPresenterImpl.class);

    private final ScannerProcessorStage scannerProcessorStage;

    private final ScannerContext scannerContext;

    private final AtomicBoolean backgroundAnalysis = new AtomicBoolean();
    private final AtomicBoolean taskRunning = new AtomicBoolean();
    private final List<Integer> selectedSongIdList = new LinkedList<>();
    private final Map<Integer, ScannerAnalysisViewModel.AnalysisResult> analysisResultMap =
            new LinkedHashMap<>();


    @MvpView
    public ScannerProcessorAnalysis.View view;

    public ScannerProcessorAnalysisPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.scannerContext = scannerContext;
    }

    private void setFunctionButtonToStart() {
        view.setFunctionButton(
                Language.INSTANCE.getString("scanner.processor.analysis.progress.startAnalysis"),
                this::startAnalysisTask_selectedSongList);
    }

    private void setFunctionButtonToStop() {
        view.setFunctionButton(
                Language.INSTANCE.getString("scanner.processor.analysis.progress.stopAnalysis"),
                this::stopAnalysisTask);
    }

    private void showAutoAnalysisDoneHeaderMessage() {
        Toolkit.getDefaultToolkit().beep();

        scannerProcessorStage.showAutoAnalysisMessage(
                Language.INSTANCE.getString("scanner.processor.analysis.header.autoAnalysisDone"));
    }

    private void startAnalysisTask_selectedSongList() {
        startAnalysisTask(scannerContext.scannerAnalysisService.getCaptureEntryIdSetFromSongIdList(
                selectedSongIdList));
    }

    private void startAnalysisTask(Set<Integer> captureEntryIdSet) {
        Task<Map<Integer, CaptureAnalysisTaskResult>> task =
                scannerContext.scannerAnalysisService.createTask(captureEntryIdSet);
        if (task == null) {
            return;
        }

        task.progressProperty().addListener(
                (observable, oldValue, newValue) -> view.setProgress(newValue.doubleValue()));

        task.setOnCancelled(this::onTaskCancelled);
        task.setOnFailed(this::onTaskFailed);
        task.setOnRunning(this::onTaskRunning);
        task.setOnSucceeded(event -> onTaskSucceeded(task.getValue()));

        taskRunning.set(true);
        CompletableFuture.runAsync(task);
    }

    private void stopAnalysisTask() {
        scannerContext.scannerAnalysisService.stopTask();
    }

    private void onTaskRunning(WorkerStateEvent ignored) {
        taskRunning.set(false);

        analysisResultMap.clear();

        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.progress.analyzing"));
        setFunctionButtonToStop();
        view.enableShowResultButton(false);
    }

    private void onTaskSucceeded(Map<Integer, CaptureAnalysisTaskResult> taskValue) {
        taskRunning.set(false);

        taskValue.forEach((key, value) -> analysisResultMap.put(key,
                ScannerAnalysisViewModel.AnalysisResult.from(value)));

        setFunctionButtonToStart();
        view.enableShowResultButton(true);

        Language language = Language.INSTANCE;

        boolean completedWithException = analysisResultMap.values().stream()
                .anyMatch(ScannerAnalysisViewModel.AnalysisResult::hasException);

        view.setMessageText(language.getString(completedWithException
                ? "scanner.processor.analysis.progress.analysisDoneWithException"
                : "scanner.processor.analysis.progress.analysisDone"));

        if (backgroundAnalysis.get()) {
            showAutoAnalysisDoneHeaderMessage();
        } else if (!analysisResultMap.values().stream()
                .allMatch(ScannerAnalysisViewModel.AnalysisResult::isAlreadyAnalyzed)) {
            if (completedWithException) {
                scannerProcessorStage.showWarning(language.getString(
                        "scanner.processor.analysis.dialog.taskCompletedWithException"));
            } else {
                scannerProcessorStage.showInformation(
                        language.getString("scanner.processor.analysis.dialog.taskCompleted"));
            }
        }

        if (!completedWithException) {
            scannerProcessorStage.setCaptureAnalyzed();
        }
    }

    private void onTaskCancelled(WorkerStateEvent ignored) {
        taskRunning.set(false);

        Language language = Language.INSTANCE;

        view.setMessageText(
                language.getString("scanner.processor.analysis.progress.analysisCancelled"));
        setFunctionButtonToStart();

        scannerProcessorStage.showInformation(
                language.getString("scanner.processor.analysis.dialog.taskCancelled"));
    }

    private void onTaskFailed(WorkerStateEvent event) {
        taskRunning.set(false);

        Language language = Language.INSTANCE;

        view.setMessageText(
                language.getString("scanner.processor.analysis.progress.analysisSuspended"));
        setFunctionButtonToStart();

        Throwable throwable = event.getSource().getException();

        LOGGER.atError().setCause(throwable).log("Analysis exception.");
        scannerProcessorStage.showError(
                language.getString("scanner.processor.analysis.dialog.taskException"), throwable);
    }

    @Override
    public void runAnalysis_allCapture() {
        Set<Integer> set = scannerContext.scannerAnalysisService.getAllCaptureEntryIdSet();
        if (set.isEmpty()) {
            return;
        }

        backgroundAnalysis.set(true);
        scannerProcessorStage.showAutoAnalysisMessage(
                Language.INSTANCE.getString("scanner.processor.analysis.header.runAutoAnalysis"));

        startAnalysisTask(set);
    }

    @Override
    public void runAnalysis_selectedSong(List<Integer> selectedSongIdList) {
        this.selectedSongIdList.addAll(selectedSongIdList);

        if (backgroundAnalysis.getAndSet(false)) {
            return;
        }

        startAnalysisTask_selectedSongList();
    }

    @Override
    public void showResult() {
        view.setResultTableItemList(FXCollections.observableArrayList(analysisResultMap.values()));
        view.showResult();
    }

    @Override
    public void hideResult() {
        view.hideResult();
    }

    @Override
    public void showResultException(Exception exception) {
        scannerProcessorStage.showError(exception.toString(), exception);
    }

    @Override
    public void showReviewView() {
        if (taskRunning.get()) {
            return;
        }

        scannerProcessorStage.changeCenterView_review();
    }

    @Override
    public void showUploadView() {
        if (taskRunning.get()) {
            return;
        }

        EnumSet<ScannerAnalysisViewModel.AnalysisResult.Status> allowed =
                EnumSet.of(ScannerAnalysisViewModel.AnalysisResult.Status.ALREADY_DONE,
                        ScannerAnalysisViewModel.AnalysisResult.Status.DONE);

        Predicate<Integer> isReadyToUpload = songId -> {
            int captureEntryId =
                    scannerContext.scannerAnalysisService.getFirstLinkedCaptureEntryId(songId);

            ScannerAnalysisViewModel.AnalysisResult result = analysisResultMap.get(captureEntryId);

            return result != null && allowed.contains(result.statusProperty().get());
        };

        if (!selectedSongIdList.stream().allMatch(isReadyToUpload)) {
            scannerProcessorStage.showWarning(Language.INSTANCE.getString(
                    "scanner.processor.analysis.dialog.notAllAnalyzed"));
            return;
        }

        scannerProcessorStage.changeCenterView_upload(selectedSongIdList);
    }
}
