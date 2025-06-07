package com.github.johypark97.varchivemacro.macro.ui.presenter;

public class MacroPresenterImpl implements Macro.MacroPresenter {
    @MvpView
    public Macro.MacroView view;

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }
}
