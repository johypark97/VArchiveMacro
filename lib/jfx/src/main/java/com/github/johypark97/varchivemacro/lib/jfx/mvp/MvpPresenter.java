package com.github.johypark97.varchivemacro.lib.jfx.mvp;

public interface MvpPresenter<T extends MvpView<?>> {
    boolean isLinked();

    void linkView(T view);

    void unlinkView();

    boolean isStarted();

    boolean startPresenter();

    boolean stopPresenter();
}
