package com.github.johypark97.varchivemacro.macro.gui.presenter;

import java.nio.file.Path;
import java.util.Set;

public interface MacroViewConfig {
    int RECORD_UPLOAD_DELAY = 40;
    int SCANNER_KEY_INPUT_DURATION = 20;

    Path getAccountPath();

    void setAccountPath(Path path);

    Path getCacheDir();

    void setCacheDir(Path path);

    Set<String> getSelectedDlcTabs();

    void setSelectedDlcTabs(Set<String> tabs);

    int getRecordUploadDelay();

    void setRecordUploadDelay(int value);

    int getScannerKeyInputDuration();

    void setScannerKeyInputDuration(int value);
}
