package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.BaseStage;
import java.io.File;

public interface HomeStage extends BaseStage {
    void startStage();

    void changeCenterView_modeSelector();

    void changeCenterView_freestyleMacro();

    void changeCenterView_collectionScanner();

    File showAccountFileSelector();

    void showSetting();

    void showOpenSourceLicense();

    void showAbout();

    void showScanner();
}
