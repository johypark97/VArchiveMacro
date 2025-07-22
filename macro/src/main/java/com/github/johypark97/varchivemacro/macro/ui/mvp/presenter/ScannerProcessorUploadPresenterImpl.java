package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.util.List;

public class ScannerProcessorUploadPresenterImpl implements ScannerProcessorUpload.Presenter {
    private final ScannerProcessorStage scannerProcessorStage;

    private List<Integer> selectedSongIdList;

    @MvpView
    public ScannerProcessorUpload.View view;

    public ScannerProcessorUploadPresenterImpl(ScannerProcessorStage scannerProcessorStage) {
        this.scannerProcessorStage = scannerProcessorStage;
    }

    @Override
    public void startView() {
    }

    @Override
    public void collectNewRecord() {
    }

    @Override
    public void collectNewRecord(List<Integer> selectedSongIdList) {
        this.selectedSongIdList = selectedSongIdList;

        collectNewRecord();
    }

    @Override
    public void showAnalysisView() {
        scannerProcessorStage.changeCenterView_analysis();
    }
}
