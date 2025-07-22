package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.BaseStage;
import java.util.List;

public interface ScannerProcessorStage extends BaseStage {
    void startStage();

    void runAutoAnalysis();

    void runAnalysis(List<Integer> selectedSongIdList);

    void collectNewRecord(List<Integer> selectedSongIdList);

    void showCaptureImageViewer();

    void changeCenterView_review();

    void changeCenterView_analysis();

    void changeCenterView_upload();
}
