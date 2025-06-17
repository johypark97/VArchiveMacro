package com.github.johypark97.varchivemacro.macro.ui.presenter;

public class ScannerScannerPresenterImpl implements ScannerScanner.ScannerScannerPresenter {
    @MvpView
    public ScannerScanner.ScannerScannerView view;

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }
}
