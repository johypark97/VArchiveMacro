package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface Setting {
    interface SettingPresenter extends Mvp.MvpPresenter<SettingView, SettingPresenter> {
        void startView();
    }


    interface SettingView extends Mvp.MvpView<SettingView, SettingPresenter> {
    }
}
