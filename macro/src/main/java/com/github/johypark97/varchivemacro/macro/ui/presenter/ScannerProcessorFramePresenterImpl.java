package com.github.johypark97.varchivemacro.macro.ui.presenter;

public class ScannerProcessorFramePresenterImpl
        implements ScannerProcessorFrame.ScannerProcessorFramePresenter {
    @MvpView
    public ScannerProcessorFrame.ScannerProcessorFrameView view;

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }
}
