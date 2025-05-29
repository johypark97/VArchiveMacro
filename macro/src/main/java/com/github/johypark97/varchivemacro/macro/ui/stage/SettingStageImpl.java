package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Setting;
import com.github.johypark97.varchivemacro.macro.ui.presenter.SettingPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.SettingViewImpl;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;

public class SettingStageImpl extends AbstractTreeableStage implements SettingStage {
    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final Runnable onStop;

    private Setting.SettingPresenter presenter;

    public SettingStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Language.INSTANCE.getString("setting.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        presenter = new SettingPresenterImpl();

        SettingViewImpl view = new SettingViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    protected boolean onStopStage() {
        onStop.run();

        return true;
    }
}
