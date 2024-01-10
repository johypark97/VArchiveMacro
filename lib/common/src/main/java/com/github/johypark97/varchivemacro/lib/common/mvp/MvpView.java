package com.github.johypark97.varchivemacro.lib.common.mvp;

public interface MvpView<T extends MvpPresenter<?>> {
    void onLinkView(T presenter);

    void onUnlinkView();

    void show();

    void hide();
}
