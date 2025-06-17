package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerScanner {
    interface ScannerScannerPresenter
            extends Mvp.MvpPresenter<ScannerScannerView, ScannerScannerPresenter> {
        void startView();

        boolean stopView();
    }


    interface ScannerScannerView extends Mvp.MvpView<ScannerScannerView, ScannerScannerPresenter> {
    }
}
