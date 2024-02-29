package com.github.johypark97.varchivemacro.lib.jfx.mvp;

import java.lang.ref.WeakReference;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class AbstractMvpView<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpView<P> {
    private Stage stage;
    private WeakReference<P> presenterReference;

    protected final P getPresenter() {
        return presenterReference.get();
    }

    protected final Stage getStage() {
        return stage;
    }

    protected abstract Stage newStage();

    private void onCloseRequestHandler(WindowEvent event) {
        event.consume();
        getPresenter().stopPresenter();
    }

    @Override
    public final boolean isLinked(P presenter) {
        return presenterReference != null && presenterReference.refersTo(presenter);
    }

    @Override
    public final void onLinkView(P presenter) {
        presenterReference = new WeakReference<>(presenter);
    }

    @Override
    public final void onUnlinkView() {
        presenterReference = null; // NOPMD
    }

    @Override
    public final boolean isStarted() {
        return stage != null;
    }

    @Override
    public final void onStartPresenter() {
        stage = newStage();
        stage.setOnCloseRequest(this::onCloseRequestHandler);
        stage.show();
    }

    @Override
    public final void onStopPresenter() {
        stage.hide();
        stage = null; // NOPMD
    }
}
