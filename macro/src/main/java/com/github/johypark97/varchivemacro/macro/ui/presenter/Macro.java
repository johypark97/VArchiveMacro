package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface Macro {
    interface MacroPresenter extends Mvp.MvpPresenter<MacroView, MacroPresenter> {
        void startView();

        boolean stopView();
    }


    interface MacroView extends Mvp.MvpView<MacroView, MacroPresenter> {
    }
}
