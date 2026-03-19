package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.libjfx.Mvp;

public interface ModeSelector {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void showCollectionScannerView();

        void showFreestyleMacroView();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
    }
}
