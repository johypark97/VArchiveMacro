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
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorAnalysisPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorFramePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorReviewPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.presenter.ScannerProcessorUploadPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorAnalysisViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorFrameViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorReviewViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.ScannerProcessorUploadViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractBaseStage;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class ScannerProcessorStageImpl extends AbstractBaseStage implements ScannerProcessorStage {
    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final StageManager stageManager;

    private final ScannerContext scannerContext;

    private final Runnable onStop;

    private boolean captureAnalyzed;
    private boolean recordUploaded;

    private ScannerProcessorFrame.Presenter framePresenter;
    private ViewPresenterPair<ScannerProcessorAnalysisViewImpl, ScannerProcessorAnalysis.Presenter>
            analysis;
    private ViewPresenterPair<ScannerProcessorReviewViewImpl, ScannerProcessorReview.Presenter>
            review;
    private ViewPresenterPair<ScannerProcessorUploadViewImpl, ScannerProcessorUpload.Presenter>
            upload;

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

    private void prepareReviewView() {
        if (review != null) {
            return;
        }

        review = new ViewPresenterPair<>(new ScannerProcessorReviewViewImpl(),
                new ScannerProcessorReviewPresenterImpl(this,
                        ContextManager.INSTANCE.getGlobalContext(), scannerContext));

        Mvp.linkViewAndPresenter(review.view, review.presenter);

        review.presenter.startView();
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
        framePresenter = new ScannerProcessorFramePresenterImpl(this);

        ScannerProcessorFrameViewImpl view = new ScannerProcessorFrameViewImpl();
        Mvp.linkViewAndPresenter(view, framePresenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());
        scene.getStylesheets().add(UiResource.TABLE_COLOR_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> changeCenterView_review());

        stage.show();
    }

    @Override
    public void runAutoAnalysis() {
        prepareAnalysisView();

        analysis.presenter.runAnalysis_allCapture();
    }

    @Override
    public void runAnalysis(List<Integer> selectedSongIdList) {
        prepareAnalysisView();

        analysis.presenter.runAnalysis_selectedSong(selectedSongIdList);
    }

    @Override
    public void showCaptureImageViewer() {
        stageManager.showCaptureImageViewer(this, scannerContext);
    }

    @Override
    public void changeCenterView_review() {
        prepareReviewView();

        if (analysis != null) {
            analysis = null; // NOPMD
        }

        framePresenter.setCenterView(review.view);

        framePresenter.changeStepDisplay(ScannerProcessorFrame.Step.REVIEW);
    }

    @Override
    public void changeCenterView_analysis() {
        prepareAnalysisView();

        if (upload != null) {
            upload = null; // NOPMD
        }

        framePresenter.setCenterView(analysis.view);

        framePresenter.changeStepDisplay(ScannerProcessorFrame.Step.ANALYSIS);
    }

    @Override
    public void changeCenterView_upload(List<Integer> selectedSongIdList) {
        if (upload != null) {
            return;
        }

        upload = new ViewPresenterPair<>(new ScannerProcessorUploadViewImpl(),
                new ScannerProcessorUploadPresenterImpl(this, scannerContext));

        Mvp.linkViewAndPresenter(upload.view, upload.presenter);

        upload.presenter.startView(selectedSongIdList);

        framePresenter.setCenterView(upload.view);

        framePresenter.changeStepDisplay(ScannerProcessorFrame.Step.UPLOAD);
    }

    @Override
    public void setCaptureAnalyzed() {
        captureAnalyzed = true;
    }

    @Override
    public void setRecordUploaded() {
        recordUploaded = true;
    }

    @Override
    public void showAutoAnalysisMessage(String text) {
        framePresenter.showHeaderMessage(text);
    }

    @Override
    protected boolean onStopStage() {
        if (TaskManager.getInstance().isRunningAny()) {
            return false;
        }

        Language language = Language.INSTANCE;
        if (!captureAnalyzed) {
            if (!showConfirmation(
                    language.getString("scanner.processor.closeConfirmation.captureNotAnalyzed"))) {
                return false;
            }
        } else if (!recordUploaded) {
            if (!showConfirmation(
                    language.getString("scanner.processor.closeConfirmation.recordNotUploaded"))) {
                return false;
            }
        }

        onStop.run();

        return true;
    }

    private record ViewPresenterPair<V, P>(V view, P presenter) {
    }
}
