package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.common.EventDebouncer;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerUploadViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ScannerProcessorUploadPresenterImpl implements ScannerProcessorUpload.Presenter {
    private final ScannerProcessorStage scannerProcessorStage;

    private final ScannerContext scannerContext;

    private final EventDebouncer updateSelectedCountTextEventDebouncer = new EventDebouncer();

    private List<Integer> selectedSongIdList;
    private ObservableList<ScannerUploadViewModel.NewRecordData> observableNewRecordDataList;

    @MvpView
    public ScannerProcessorUpload.View view;

    public ScannerProcessorUploadPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.scannerContext = scannerContext;

        updateSelectedCountTextEventDebouncer.setCallback(() -> view.updateSelectedCountText());
    }

    @Override
    public void startView() {
    }

    @Override
    public void collectNewRecord() {
        scannerContext.scannerUploadService.clear();
        scannerContext.scannerUploadService.collect(selectedSongIdList);

        observableNewRecordDataList =
                scannerContext.scannerUploadService.getAllNewRecordList().stream()
                        .map(ScannerUploadViewModel.NewRecordData::from)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
        observableNewRecordDataList.forEach(
                x -> x.setOnSelectedChange(updateSelectedCountTextEventDebouncer::trigger));
        view.setRecordTableItemList(observableNewRecordDataList);
    }

    @Override
    public void collectNewRecord(List<Integer> selectedSongIdList) {
        this.selectedSongIdList = selectedSongIdList;

        collectNewRecord();
    }

    @Override
    public void upload() {
    }

    @Override
    public void showAnalysisView() {
        scannerProcessorStage.changeCenterView_analysis();
    }
}
