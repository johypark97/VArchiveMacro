package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.BaseStage;
import java.io.File;

public interface SettingStage extends BaseStage {
    void startStage();

    CloseDialogChoice showCloseDialog();

    File showAccountFileSelector();

    File showCacheDirectorySelector();

    enum CloseDialogChoice {
        APPLY,
        CANCEL,
        CLOSE
    }
}
