package com.github.johypark97.varchivemacro.lib.common.mvp;

import java.util.function.Supplier;
import javafx.stage.Stage;

public abstract class AbstractMvpView<P extends MvpPresenter<V>, V extends MvpView<P>>
        implements MvpView<P> {
    private final Stage stage = new Stage();

    private Supplier<P> presenterSupplier;

    public AbstractMvpView() {
        stage.setOnCloseRequest(event -> {
            event.consume();
            getPresenter().stop();
        });
    }

    protected final P getPresenter() {
        return presenterSupplier.get();
    }

    protected final Stage getStage() {
        return stage;
    }

    @Override
    public final void onLinkView(P presenter) {
        presenterSupplier = () -> presenter;
    }

    @Override
    public final void onUnlinkView() {
        presenterSupplier = null; // NOPMD
    }

    @Override
    public final void show() {
        stage.show();
    }

    @Override
    public final void hide() {
        stage.hide();
    }
}
