package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;

public interface ScannerTesterStage extends TreeableStage {
    void startStage();

    void showError(String content, Throwable throwable);

    void showWarning(String content);

    void showInformation(String header, String content);
}
