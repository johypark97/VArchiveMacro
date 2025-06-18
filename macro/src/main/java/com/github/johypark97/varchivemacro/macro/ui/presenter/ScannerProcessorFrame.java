package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerProcessorFrame {
    interface ScannerProcessorFramePresenter
            extends Mvp.MvpPresenter<ScannerProcessorFrameView, ScannerProcessorFramePresenter> {
        void startView();

        boolean stopView();
    }


    interface ScannerProcessorFrameView
            extends Mvp.MvpView<ScannerProcessorFrameView, ScannerProcessorFramePresenter> {
    }
}
