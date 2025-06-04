package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.macro.ui.stage.base.TreeableStage;
import java.io.File;

public interface SettingStage extends TreeableStage {
    void startStage();

    void showError(String content, Throwable throwable);

    void showError(String header, String content, Throwable throwable);

    CloseDialogChoice showCloseDialog();

    File showAccountFileSelector();

    File showCacheDirectorySelector();

    enum CloseDialogChoice {
        APPLY,
        CANCEL,
        CLOSE
    }
}
