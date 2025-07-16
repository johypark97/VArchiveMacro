package com.github.johypark97.varchivemacro.macro.ui.stage.impl;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerTester;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerTesterPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerTesterViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerTesterStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;

public class ScannerTesterStageImpl extends AbstractBaseStage implements ScannerTesterStage {
    private static final int STAGE_HEIGHT = 900;
    private static final int STAGE_WIDTH = 1600;

    private ScannerTester.Presenter presenter;

    public ScannerTesterStageImpl(AbstractTreeableStage parent) {
        super(parent);

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Language.INSTANCE.getString("scanner.tester.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        presenter =
                new ScannerTesterPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext());

        ScannerTesterViewImpl view = new ScannerTesterViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    protected boolean onStopStage() {
        return true;
    }
}
