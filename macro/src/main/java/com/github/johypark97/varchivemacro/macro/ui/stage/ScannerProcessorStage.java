package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;

public interface ScannerProcessorStage extends TreeableStage {
    void startStage();

    void focusStage();

    void showCaptureImageViewer();

    void changeCenterView_review();
}
