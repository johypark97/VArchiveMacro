package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
    }


    interface HomeView extends MvpView<HomePresenter> {
    }
}
