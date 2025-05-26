package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;

public interface HomeStage extends TreeableStage {
    void startStage();

    void showError(String header, Throwable throwable);

    void showInformation(String header, String content);

    boolean showConfirmation(String header, String content);

    void changeCenterView_modeSelector();
}
