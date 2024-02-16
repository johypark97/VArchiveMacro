package com.github.johypark97.varchivemacro.lib.common.mvp;

public interface MvpPresenter<T extends MvpView<?>> {
    void onLinkPresenter(T view);
}
