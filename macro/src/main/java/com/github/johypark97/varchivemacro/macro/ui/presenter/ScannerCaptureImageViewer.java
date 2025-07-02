package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerCaptureImageViewer {
    interface ScannerCaptureImageViewerPresenter extends
            Mvp.MvpPresenter<ScannerCaptureImageViewerView, ScannerCaptureImageViewerPresenter> {
        void startView();
    }


    interface ScannerCaptureImageViewerView
            extends Mvp.MvpView<ScannerCaptureImageViewerView, ScannerCaptureImageViewerPresenter> {
    }
}
