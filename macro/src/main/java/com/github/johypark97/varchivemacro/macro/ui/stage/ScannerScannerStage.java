package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;

public interface ScannerScannerStage extends TreeableStage {
    void startStage();

    void focusStage();

    void showError(String content, Throwable throwable);

    void showError(String header, String content, Throwable throwable);

    void showWarning(String content);

    void showInformation(String content);

    void showInformation(String header, String content);
}
