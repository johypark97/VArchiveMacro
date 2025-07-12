package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerProcessorReview {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
    }
}
