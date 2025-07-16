package com.github.johypark97.varchivemacro.macro.ui.stage.impl;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerCaptureImageViewer;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerCaptureImageViewerPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerCaptureImageViewerViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerCaptureImageViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerCaptureImageViewerStageImpl extends AbstractBaseStage
        implements ScannerCaptureImageViewerStage {
    private static final int STAGE_HEIGHT = 900;
    private static final int STAGE_WIDTH = 1600;

    private final ScannerContext scannerContext;

    private final Runnable onStop;

    private ScannerCaptureImageViewer.Presenter presenter;

    public ScannerCaptureImageViewerStageImpl(AbstractTreeableStage parent,
            ScannerContext scannerContext, Runnable onStop) {
        super(parent);

        this.scannerContext = scannerContext;

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(Language.INSTANCE.getString("scanner.captureImageViewer.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        presenter = new ScannerCaptureImageViewerPresenterImpl(this, scannerContext);

        ScannerCaptureImageViewerViewImpl view = new ScannerCaptureImageViewerViewImpl();
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
