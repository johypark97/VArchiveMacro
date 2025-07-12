package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;

public class ScannerProcessorFramePresenterImpl implements ScannerProcessorFrame.Presenter {
    private final ScannerProcessorStage scannerProcessorStage;

    @MvpView
    public ScannerProcessorFrame.View view;

    public ScannerProcessorFramePresenterImpl(ScannerProcessorStage scannerProcessorStage) {
        this.scannerProcessorStage = scannerProcessorStage;
    }

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }

    @Override
    public void showCaptureImageViewer() {
        scannerProcessorStage.showCaptureImageViewer();
    }
}
