package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerProcessorFrame {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void showCaptureImageViewer();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
    }
}
