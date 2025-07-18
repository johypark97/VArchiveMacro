package com.github.johypark97.varchivemacro.macro.ui.stage.impl;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.manager.StageManager;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorReview;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorAnalysisPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorFramePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorReviewPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorAnalysisViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorFrameViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorReviewViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerProcessorStageImpl extends AbstractBaseStage implements ScannerProcessorStage {
    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final StageManager stageManager;

    private final ScannerContext scannerContext;

    private final Runnable onStop;

    private ScannerProcessorFrame.Presenter framePresenter;
    private ViewPresenterPair<ScannerProcessorAnalysisViewImpl, ScannerProcessorAnalysis.Presenter>
            analysis;
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

    private void prepareAnalysisView() {
        if (analysis != null) {
            return;
        }

        analysis = new ViewPresenterPair<>(new ScannerProcessorAnalysisViewImpl(),
                new ScannerProcessorAnalysisPresenterImpl(this, scannerContext));

        Mvp.linkViewAndPresenter(analysis.view, analysis.presenter);
    }

    @Override
    public void startStage() {
        framePresenter = new ScannerProcessorFramePresenterImpl(this,
                ContextManager.INSTANCE.getGlobalContext());

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
    public void showCaptureImageViewer() {
        stageManager.showCaptureImageViewer(this, scannerContext);
    }

    @Override
    public void runAutoAnalysis() {
        prepareAnalysisView();

        Platform.runLater(() -> analysis.presenter.runAnalysis_allCapture());
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
    public void changeCenterView_analysis(List<Integer> selectedSongIdList) {
        prepareAnalysisView();

        Platform.runLater(() -> analysis.presenter.runAnalysis_selectedSong(selectedSongIdList));

        framePresenter.setCenterView(analysis.view);
    }

    @Override
    protected boolean onStopStage() {
        if (TaskManager.getInstance().isRunningAny()) {
            return false;
        }

        onStop.run();

        return true;
    }

    private record ViewPresenterPair<V, P>(V view, P presenter) {
    }
}
