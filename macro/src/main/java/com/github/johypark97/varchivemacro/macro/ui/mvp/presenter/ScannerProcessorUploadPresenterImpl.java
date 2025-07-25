package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload.SongRecordUploadTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.common.EventDebouncer;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerUploadViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerProcessorUploadPresenterImpl implements ScannerProcessorUpload.Presenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScannerProcessorUploadPresenterImpl.class);

    private final ScannerProcessorStage scannerProcessorStage;

    private final ScannerContext scannerContext;

    private final AtomicBoolean taskRunning = new AtomicBoolean();
    private final EventDebouncer updateSelectedCountTextEventDebouncer = new EventDebouncer();

    private List<Integer> selectedSongIdList;
    private Map<Integer, ScannerUploadViewModel.NewRecordData> newRecordDataLookup;
    private ObservableList<ScannerUploadViewModel.NewRecordData> observableNewRecordDataList;

    @MvpView
    public ScannerProcessorUpload.View view;

    public ScannerProcessorUploadPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.scannerContext = scannerContext;

        updateSelectedCountTextEventDebouncer.setCallback(() -> view.updateSelectedCountText());
    }

    private void onTaskRunning(WorkerStateEvent ignored) {
        taskRunning.set(true);

        view.showProgressBox();
    }

    private void onTaskStopped(WorkerStateEvent event) {
        taskRunning.set(false);

        Language language = Language.INSTANCE;

        Throwable throwable = event.getSource().getException();
        if (throwable == null) {
            scannerProcessorStage.showInformation(
                    language.getString("scanner.processor.upload.dialog.upload.done"));
        } else {
            LOGGER.atError().setCause(throwable).log("Record upload exception.");
            scannerProcessorStage.showError(
                    language.getString("scanner.processor.upload.dialog.upload.exception"),
                    throwable.getMessage(), throwable);
        }

        @SuppressWarnings("unchecked")
        List<SongRecordUploadTaskResult> resultList =
                (List<SongRecordUploadTaskResult>) event.getSource().getValue();
        if (resultList != null) {
            resultList.forEach(x -> {
                ScannerUploadViewModel.NewRecordData data = newRecordDataLookup.get(x.entryId);
                if (data == null) {
                    return;
                }

                data.resultProperty().set(ScannerUploadViewModel.Result.from(x.getStatus()));
            });
        }

        view.hideProgressBox();
    }

    @Override
    public void startView() {
    }

    @Override
    public void collectNewRecord() {
        if (taskRunning.get()) {
            return;
        }

        scannerContext.scannerUploadService.clear();
        scannerContext.scannerUploadService.collect(selectedSongIdList);

        observableNewRecordDataList =
                scannerContext.scannerUploadService.getAllNewRecordList().stream()
                        .map(ScannerUploadViewModel.NewRecordData::from)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
        observableNewRecordDataList.forEach(
                x -> x.setOnSelectedChange(updateSelectedCountTextEventDebouncer::trigger));
        view.setRecordTableItemList(observableNewRecordDataList);

        newRecordDataLookup = observableNewRecordDataList.stream().collect(
                Collectors.toMap(ScannerUploadViewModel.NewRecordData::getUpdatedSongRecordEntryId,
                        Function.identity()));
    }

    @Override
    public void collectNewRecord(List<Integer> selectedSongIdList) {
        this.selectedSongIdList = selectedSongIdList;

        collectNewRecord();
    }

    @Override
    public void upload() {
        Language language = Language.INSTANCE;

        List<Integer> selectedUpdatedSongRecordEntryIdList =
                observableNewRecordDataList.stream().filter(x -> x.selectedProperty().get())
                        .map(ScannerUploadViewModel.NewRecordData::getUpdatedSongRecordEntryId)
                        .toList();

        if (selectedUpdatedSongRecordEntryIdList.isEmpty()) {
            scannerProcessorStage.showWarning(
                    language.getString("scanner.processor.upload.noRecordSelected"));
            return;
        }

        if (!scannerProcessorStage.showConfirmation(
                language.getFormatString("scanner.processor.upload.uploadConfirmation",
                        selectedUpdatedSongRecordEntryIdList.size()))) {
            return;
        }

        Task<List<SongRecordUploadTaskResult>> task =
                scannerContext.scannerUploadService.createTask(
                        selectedUpdatedSongRecordEntryIdList);
        if (task == null) {
            return;
        }

        task.progressProperty().addListener(
                (observable, oldValue, newValue) -> view.setProgress(newValue.doubleValue()));

        task.setOnCancelled(this::onTaskStopped);
        task.setOnFailed(this::onTaskStopped);
        task.setOnRunning(this::onTaskRunning);
        task.setOnSucceeded(this::onTaskStopped);

        CompletableFuture.runAsync(task);
    }

    @Override
    public void showAnalysisView() {
        if (taskRunning.get()) {
            return;
        }

        scannerProcessorStage.changeCenterView_analysis();
    }
}
