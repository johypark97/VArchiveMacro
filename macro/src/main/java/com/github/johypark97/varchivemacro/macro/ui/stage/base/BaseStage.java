package com.github.johypark97.varchivemacro.macro.ui.stage.base;

public interface BaseStage extends TreeableStage {
    void focusStage();

    void showError(String content, Throwable throwable);

    void showError(String header, String content, Throwable throwable);

    void showWarning(String content);

    void showWarning(String header, String content);

    void showInformation(String content);

    void showInformation(String header, String content);

    boolean showConfirmation(String content);

    boolean showConfirmation(String header, String content);
}
