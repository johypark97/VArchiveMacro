package com.github.johypark97.varchivemacro.macro.ui.presenter;

public class ScannerHomePresenterImpl implements ScannerHome.ScannerHomePresenter {
    @MvpView
    public ScannerHome.ScannerHomeView view;

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }
}
