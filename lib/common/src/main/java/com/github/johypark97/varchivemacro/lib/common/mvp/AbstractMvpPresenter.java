package com.github.johypark97.varchivemacro.lib.common.mvp;

import java.lang.ref.WeakReference;

public abstract class AbstractMvpPresenter<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpPresenter<V> {
    private WeakReference<V> viewReference;

    protected final V getView() {
        return viewReference.get();
    }

    @Override
    public final void onLinkPresenter(V view) {
        if (viewReference != null) {
            return;
        }

        viewReference = new WeakReference<>(view);
    }
}
