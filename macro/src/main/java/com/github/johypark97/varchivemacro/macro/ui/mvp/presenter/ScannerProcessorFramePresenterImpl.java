package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import javafx.scene.Node;

public class ScannerProcessorFramePresenterImpl implements ScannerProcessorFrame.Presenter {
    private final ScannerProcessorStage scannerProcessorStage;

    @MvpView
    public ScannerProcessorFrame.View view;

    public ScannerProcessorFramePresenterImpl(ScannerProcessorStage scannerProcessorStage) {
        this.scannerProcessorStage = scannerProcessorStage;
    }

    @Override
    public void changeStepDisplay(ScannerProcessorFrame.Step step) {
        switch (step) {
            case REVIEW -> view.setStepDisplay_review();
            case ANALYSIS -> view.setStepDisplay_analysis();
            case UPLOAD -> view.setStepDisplay_upload();
        }
    }

    @Override
    public void showHeaderMessage(String text) {
        view.setHeaderMessage(text);
    }

    @Override
    public void showCaptureImageViewer() {
        scannerProcessorStage.showCaptureImageViewer();
    }

    @Override
    public <T extends Node & ScannerProcessorFrame.ViewButtonController> void setCenterView(
            T value) {
        view.setCenterNode(value);
        view.setLeftButtonFunction(value.getLeftButtonFunction());
        view.setRightButtonFunction(value.getRightButtonFunction());
    }
}
