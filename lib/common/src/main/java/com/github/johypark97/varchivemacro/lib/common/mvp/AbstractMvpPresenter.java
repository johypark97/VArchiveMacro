package com.github.johypark97.varchivemacro.lib.common.mvp;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractMvpPresenter<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpPresenter<V> {
    private final Supplier<V> viewConstructor;

    private Supplier<V> viewSupplier;

    public AbstractMvpPresenter(Supplier<V> viewConstructor) {
        this.viewConstructor = viewConstructor;
    }

    protected abstract P getInstance();

    protected boolean initialize() {
        return true;
    }

    protected boolean terminate() {
        return true;
    }

    protected final V getView() {
        return viewSupplier.get();
    }

    private boolean isViewLinked() {
        return viewSupplier != null;
    }

    @Override
    public final void linkView(V view) {
        viewSupplier = () -> view;
        viewSupplier.get().onLinkView(Objects.requireNonNull(getInstance()));
    }

    @Override
    public final void unlinkView() {
        viewSupplier.get().onUnlinkView();
        viewSupplier = null; // NOPMD
    }

    @Override
    public final boolean start() {
        if (isViewLinked()) {
            return false;
        }

        if (!initialize()) {
            return false;
        }

        linkView(viewConstructor.get());
        getView().show();

        return true;
    }

    @Override
    public final boolean stop() {
        if (!isViewLinked()) {
            return false;
        }

        if (!terminate()) {
            return false;
        }

        getView().hide();
        unlinkView();

        return true;
    }
}
