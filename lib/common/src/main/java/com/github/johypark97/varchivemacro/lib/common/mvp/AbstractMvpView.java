package com.github.johypark97.varchivemacro.lib.common.mvp;

import java.util.Objects;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public abstract class AbstractMvpView<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpView<P> {
    private P presenter;
    private Stage stage;

    protected final P getPresenter() {
        return presenter;
    }

    protected final Stage getStage() {
        return stage;
    }

    protected abstract V getInstance();

    protected abstract Stage newStage();

    protected boolean onStartView() {
        return true;
    }

    protected boolean onStopView() {
        return true;
    }

    private void onCloseRequestHandler(WindowEvent event) {
        event.consume();
        stopView();
    }

    @Override
    public final void linkPresenter(P presenter) {
        if (this.presenter != null) {
            return;
        }

        this.presenter = presenter;
        this.presenter.onLinkPresenter(Objects.requireNonNull(getInstance()));
    }

    @Override
    public void requestFocus() {
        stage.requestFocus();
    }

    @Override
    public final boolean isStarted() {
        return stage != null;
    }

    @Override
    public final boolean startView() {
        Objects.requireNonNull(presenter);

        if (stage != null || !onStartView()) {
            return false;
        }

        stage = newStage();
        stage.setOnCloseRequest(this::onCloseRequestHandler);
        stage.show();

        return true;
    }

    @Override
    public final boolean stopView() {
        Objects.requireNonNull(presenter);

        if (stage == null || !onStopView()) {
            return false;
        }

        stage.hide();
        stage = null; // NOPMD

        return true;
    }
}
