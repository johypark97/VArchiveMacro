package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFramePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerProcessorFrameViewImpl;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerProcessorStageImpl extends AbstractTreeableStage
        implements ScannerProcessorStage {
    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final Runnable onStop;

    private ScannerProcessorFrame.ScannerProcessorFramePresenter framePresenter;

    public ScannerProcessorStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(Language.INSTANCE.getString("scanner.processor.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        framePresenter = new ScannerProcessorFramePresenterImpl();

        ScannerProcessorFrameViewImpl view = new ScannerProcessorFrameViewImpl();
        Mvp.linkViewAndPresenter(view, framePresenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> framePresenter.startView());

        stage.show();
    }

    @Override
    public void focusStage() {
        stage.requestFocus();
    }

    @Override
    protected boolean onStopStage() {
        if (!framePresenter.stopView()) {
            return false;
        }

        onStop.run();

        return true;
    }
}
