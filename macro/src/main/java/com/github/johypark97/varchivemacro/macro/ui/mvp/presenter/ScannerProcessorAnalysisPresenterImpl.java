package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerAnalysisViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

    private final AtomicBoolean analysisCompleted = new AtomicBoolean();

    private List<Integer> selectedSongIdList;
    private Map<Integer, ScannerAnalysisViewModel.AnalysisResult> analysisResultMap;

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

        CompletableFuture.runAsync(task);
    }

    private void stopAnalysisTask() {
        scannerContext.scannerAnalysisService.stopTask();
    }

    private void onTaskRunning(WorkerStateEvent ignored) {
        analysisCompleted.set(false);

        analysisResultMap = null; // NOPMD

        Language language = Language.INSTANCE;

        view.setMessageText(language.getString("scanner.processor.analysis.progress.analyzing"));
        setFunctionButtonToStop();
        view.enableShowResultButton(false);
    }

    private void onTaskSucceeded(Map<Integer, CaptureAnalysisTaskResult> taskValue) {
        analysisCompleted.set(true);

        analysisResultMap = taskValue.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey,
                        x -> ScannerAnalysisViewModel.AnalysisResult.from(x.getValue())));

        setFunctionButtonToStart();
        view.enableShowResultButton(true);

        Language language = Language.INSTANCE;

        if (analysisResultMap.values().stream()
                .anyMatch(ScannerAnalysisViewModel.AnalysisResult::hasException)) {
            view.setMessageText(language.getString(
                    "scanner.processor.analysis.progress.analysisDoneWithException"));
            scannerProcessorStage.showWarning(language.getString(
                    "scanner.processor.analysis.dialog.taskCompletedWithException"));
        } else {
            view.setMessageText(
                    language.getString("scanner.processor.analysis.progress.analysisDone"));
            scannerProcessorStage.showInformation(
                    language.getString("scanner.processor.analysis.dialog.taskCompleted"));
        }
    }

    private void onTaskCancelled(WorkerStateEvent ignored) {
        Language language = Language.INSTANCE;

        view.setMessageText(
                language.getString("scanner.processor.analysis.progress.analysisCancelled"));
        setFunctionButtonToStart();

        scannerProcessorStage.showInformation(
                language.getString("scanner.processor.analysis.dialog.taskCancelled"));
    }

    private void onTaskFailed(WorkerStateEvent event) {
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
    public void startView() {
        view.setProgress(0);
        view.setMessageText("");
        setFunctionButtonToStart();
    }

    @Override
    public void runAnalysis_allCapture() {
        Set<Integer> set = scannerContext.scannerAnalysisService.getAllCaptureEntryIdSet();
        if (set.isEmpty()) {
            return;
        }

        startAnalysisTask(set);
    }

    @Override
    public void runAnalysis_selectedSong(List<Integer> selectedSongIdList) {
        this.selectedSongIdList = selectedSongIdList;

        if (analysisCompleted.get()) {
            return;
        }

        startAnalysisTask_selectedSongList();
    }

    @Override
    public void showResult() {
        if (analysisResultMap == null) {
            return;
        }

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
        scannerProcessorStage.changeCenterView_review();
    }

    @Override
    public void showUploadView() {
        EnumSet<ScannerAnalysisViewModel.AnalysisResult.Status> allowed =
                EnumSet.of(ScannerAnalysisViewModel.AnalysisResult.Status.ALREADY_DONE,
                        ScannerAnalysisViewModel.AnalysisResult.Status.DONE);

        Predicate<Integer> isReadyToUpload = songId -> {
            int captureEntryId =
                    scannerContext.scannerAnalysisService.getFirstLinkedCaptureEntryId(songId);

            ScannerAnalysisViewModel.AnalysisResult result = analysisResultMap.get(captureEntryId);

            return result != null && allowed.contains(result.statusProperty().get());
        };

        if (selectedSongIdList == null || analysisResultMap == null || !selectedSongIdList.stream()
                .allMatch(isReadyToUpload)) {
            scannerProcessorStage.showWarning(Language.INSTANCE.getString(
                    "scanner.processor.analysis.dialog.notAllAnalyzed"));
            return;
        }

        scannerProcessorStage.changeCenterView_upload();
        scannerProcessorStage.collectNewRecord(selectedSongIdList);
    }
}
