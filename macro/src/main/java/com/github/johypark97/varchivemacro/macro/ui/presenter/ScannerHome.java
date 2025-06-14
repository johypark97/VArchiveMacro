package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface ScannerHome {
    interface ScannerHomePresenter extends Mvp.MvpPresenter<ScannerHomeView, ScannerHomePresenter> {
        void startView();

        boolean stopView();
    }


    interface ScannerHomeView extends Mvp.MvpView<ScannerHomeView, ScannerHomePresenter> {
    }
}
