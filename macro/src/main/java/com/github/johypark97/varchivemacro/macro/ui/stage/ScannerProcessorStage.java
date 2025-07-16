package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.BaseStage;

public interface ScannerProcessorStage extends BaseStage {
    void startStage();

    void showCaptureImageViewer();

    void changeCenterView_review();
}
