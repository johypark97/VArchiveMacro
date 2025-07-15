package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.manager.StageManager;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFramePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorReview;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorReviewPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerProcessorFrameViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.ScannerProcessorReviewViewImpl;
import java.awt.Toolkit;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;

public class ScannerProcessorStageImpl extends AbstractTreeableStage
        implements ScannerProcessorStage {
    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final StageManager stageManager;

    private final ScannerContext scannerContext;

    private final Runnable onStop;

    private ScannerProcessorFrame.Presenter framePresenter;
    private ViewPresenterPair<ScannerProcessorReviewViewImpl, ScannerProcessorReview.Presenter>
            review;

    public ScannerProcessorStageImpl(AbstractTreeableStage parent, StageManager stageManager,
            ScannerContext scannerContext, Runnable onStop) {
        super(parent);

        this.stageManager = stageManager;

        this.scannerContext = scannerContext;

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
        framePresenter = new ScannerProcessorFramePresenterImpl(this);

        ScannerProcessorFrameViewImpl view = new ScannerProcessorFrameViewImpl();
        Mvp.linkViewAndPresenter(view, framePresenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());
        scene.getStylesheets().add(UiResource.TABLE_COLOR_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> framePresenter.startView());

        stage.show();
    }

    @Override
    public void focusStage() {
        stage.requestFocus();
    }

    @Override
    public void showError(String content, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setContentText(content)
                .setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showCaptureImageViewer() {
        stageManager.showCaptureImageViewer(this, scannerContext);
    }

    @Override
    public void changeCenterView_review() {
        if (review == null) {
            review = new ViewPresenterPair<>(new ScannerProcessorReviewViewImpl(),
                    new ScannerProcessorReviewPresenterImpl(this, scannerContext));

            Mvp.linkViewAndPresenter(review.view, review.presenter);
        }

        framePresenter.setCenterView(review.view);
    }

    @Override
    protected boolean onStopStage() {
        if (!framePresenter.stopView()) {
            return false;
        }

        onStop.run();

        return true;
    }

    private record ViewPresenterPair<V, P>(V view, P presenter) {
    }
}
