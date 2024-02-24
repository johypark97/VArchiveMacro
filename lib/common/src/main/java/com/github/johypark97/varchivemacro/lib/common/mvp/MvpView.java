package com.github.johypark97.varchivemacro.lib.common.mvp;

public interface MvpView<T extends MvpPresenter<?>> {
    boolean isLinked(T presenter);

    void onLinkView(T presenter);

    void onUnlinkView();

    boolean isStarted();

    void onStartPresenter();

    void onStopPresenter();
}
