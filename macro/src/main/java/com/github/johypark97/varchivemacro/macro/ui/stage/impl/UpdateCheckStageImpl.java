package com.github.johypark97.varchivemacro.macro.ui.stage.impl;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.ui.mvp.UpdateCheck;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.UpdateCheckPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.UpdateCheckViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.UpdateCheckStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;

public class UpdateCheckStageImpl extends AbstractBaseStage implements UpdateCheckStage {
    private static final int STAGE_HEIGHT = 360;
    private static final int STAGE_WIDTH = 640;

    private final Runnable onStop;

    private UpdateCheck.Presenter presenter;

    public UpdateCheckStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Language.INSTANCE.getString("updateCheck.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);
    }

    @Override
    public void startStage() {
        presenter = new UpdateCheckPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext(),
                ContextManager.INSTANCE.getUpdateCheckContext());

        UpdateCheckViewImpl view = new UpdateCheckViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    protected boolean onStopStage() {
        if (!presenter.stopView()) {
            return false;
        }

        onStop.run();

        return true;
    }
}
