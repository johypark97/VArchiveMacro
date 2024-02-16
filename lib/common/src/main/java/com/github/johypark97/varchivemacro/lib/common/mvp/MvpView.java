package com.github.johypark97.varchivemacro.lib.common.mvp;

public interface MvpView<T extends MvpPresenter<?>> {
    void linkPresenter(T presenter);

    boolean startView();

    boolean stopView();
}
