package com.github.johypark97.varchivemacro.lib.jfx.mvp;

import java.util.Objects;

public abstract class AbstractMvpPresenter<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpPresenter<V> {
    private V view;

    protected final V getView() {
        return view;
    }

    protected abstract P getInstance();

    protected boolean initialize() {
        return true;
    }

    protected boolean terminate() {
        return true;
    }

    @Override
    public final boolean isLinked() {
        return view != null && view.isLinked(Objects.requireNonNull(getInstance()));
    }

    @Override
    public final void linkView(V view) {
        if (isLinked()) {
            return;
        }

        this.view = Objects.requireNonNull(view);
        this.view.onLinkView(Objects.requireNonNull(getInstance()));
    }

    @Override
    public final void unlinkView() {
        if (!isLinked()) {
            return;
        }

        view.onUnlinkView();
        view = null; // NOPMD
    }

    @Override
    public final boolean isStarted() {
        return isLinked() && view.isStarted();
    }

    @Override
    public final boolean startPresenter() {
        synchronized (this) {
            if (isStarted() || !initialize()) {
                return false;
            }

            view.onStartPresenter();
        }

        return true;
    }

    @Override
    public final boolean stopPresenter() {
        synchronized (this) {
            if (!isStarted() || !terminate()) {
                return false;
            }

            view.onStopPresenter();
        }

        return true;
    }
}
