package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import java.nio.file.Path;
import java.util.Set;

public interface MacroViewConfig {
    int KEY_INPUT_DURATION = 40;
    int MACRO_CAPTURE_DELAY = 500;
    int MACRO_CAPTURE_DURATION = 20;
    int MACRO_COUNT = 100;
    int RECORD_UPLOAD_DELAY = 40;
    int SCANNER_CAPTURE_DELAY = 0;

    Path getAccountPath();

    void setAccountPath(Path path);

    Path getCacheDir();

    void setCacheDir(Path path);

    Set<String> getSelectedDlcTabs();

    void setSelectedDlcTabs(Set<String> tabs);

    int getRecordUploadDelay();

    void setRecordUploadDelay(int value);

    int getScannerCaptureDelay();

    void setScannerCaptureDelay(int value);

    int getScannerKeyInputDuration();

    void setScannerKeyInputDuration(int value);

    AnalyzeKey getMacroAnalyzeKey();

    void setMacroAnalyzeKey(AnalyzeKey value);

    int getMacroCount();

    void setMacroCount(int value);

    int getMacroCaptureDelay();

    void setMacroCaptureDelay(int value);

    int getMacroCaptureDuration();

    void setMacroCaptureDuration(int value);

    int getMacroKeyInputDuration();

    void setMacroKeyInputDuration(int value);
}
