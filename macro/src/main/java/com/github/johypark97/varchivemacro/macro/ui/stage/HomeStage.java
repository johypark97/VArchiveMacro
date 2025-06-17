package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;
import java.io.File;

public interface HomeStage extends TreeableStage {
    void startStage();

    void showError(String content, Throwable throwable);

    void showError(String header, String content, Throwable throwable);

    void showWarning(String content);

    void showInformation(String header, String content);

    boolean showConfirmation(String header, String content);

    void changeCenterView_modeSelector();

    void changeCenterView_freestyleMacro();

    void changeCenterView_collectionScanner();

    File showAccountFileSelector();

    void showSetting();

    void showOpenSourceLicense();

    void showAbout();

    void showScanner();
}
