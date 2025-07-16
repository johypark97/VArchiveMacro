package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import javafx.application.Platform;
import javafx.scene.Node;

public class ScannerProcessorFramePresenterImpl implements ScannerProcessorFrame.Presenter {
    private final ScannerProcessorStage scannerProcessorStage;

    private final GlobalContext globalContext;

    @MvpView
    public ScannerProcessorFrame.View view;

    public ScannerProcessorFramePresenterImpl(ScannerProcessorStage scannerProcessorStage,
            GlobalContext globalContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.globalContext = globalContext;
    }

    @Override
    public void startView() {
        scannerProcessorStage.changeCenterView_review();
        Platform.runLater(view::runLeftButtonAction);

        if (globalContext.configService.findScannerConfig().autoAnalysis()) {
            Platform.runLater(scannerProcessorStage::runAutoAnalysis);
        }
    }

    @Override
    public boolean stopView() {
        return true;
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
