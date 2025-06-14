package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;

public interface HomeStage extends TreeableStage {
    void startStage();

    void showError(String content, Throwable throwable);

    void showError(String header, String content, Throwable throwable);

    void showInformation(String header, String content);

    boolean showConfirmation(String header, String content);

    void changeCenterView_modeSelector();

    void changeCenterView_freestyleMacro();

    void changeCenterView_collectionScanner();

    void showSetting();

    void showOpenSourceLicense();

    void showAbout();
}
