package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.BaseStage;
import java.util.List;

public interface ScannerProcessorStage extends BaseStage {
    void startStage();

    void showCaptureImageViewer();

    void runAutoAnalysis();

    void changeCenterView_review();

    void changeCenterView_analysis(List<Integer> selectedSongIdList);
}
