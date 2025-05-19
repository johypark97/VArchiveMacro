package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ModeSelector {
    interface ModeSelectorPresenter
            extends Mvp.MvpPresenter<ModeSelectorView, ModeSelectorPresenter> {
        void startView();

        boolean stopView();

        void showCollectionScannerView();

        void showFreestyleMacroView();
    }


    interface ModeSelectorView extends Mvp.MvpView<ModeSelectorView, ModeSelectorPresenter> {
    }
}
