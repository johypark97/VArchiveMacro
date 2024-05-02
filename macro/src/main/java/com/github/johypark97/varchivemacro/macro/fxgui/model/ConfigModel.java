package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface ConfigModel {
    boolean load() throws IOException;

    void save() throws IOException;

    ScannerConfig getScannerConfig();

    MacroConfig getMacroConfig();

    void setScannerConfig(ScannerConfig value);

    void setMacroConfig(MacroConfig value);

    class ScannerConfig {
        public static final Path ACCOUNT_FILE_DEFAULT = Path.of("account.txt");
        public static final Path CACHE_DIRECTORY_DEFAULT = Path.of("cache");

        public static final int CAPTURE_DELAY_DEFAULT = 0;
        public static final int CAPTURE_DELAY_MAX = 1000;
        public static final int CAPTURE_DELAY_MIN = 0;

        public static final int KEY_INPUT_DURATION_DEFAULT = 40;
        public static final int KEY_INPUT_DURATION_MAX = 100;
        public static final int KEY_INPUT_DURATION_MIN = 20;

        public static final int RECORD_UPLOAD_DELAY_DEFAULT = 40;
        public static final int RECORD_UPLOAD_DELAY_MAX = 1000;
        public static final int RECORD_UPLOAD_DELAY_MIN = 0;

        @Expose
        public Set<String> selectedTabSet = Set.of();

        @Expose
        public Path cacheDirectory = CACHE_DIRECTORY_DEFAULT;

        @Expose
        public int captureDelay = CAPTURE_DELAY_DEFAULT;

        @Expose
        public int keyInputDuration = KEY_INPUT_DURATION_DEFAULT;

        @Expose
        public Path accountFile = ACCOUNT_FILE_DEFAULT;

        @Expose
        public int recordUploadDelay = RECORD_UPLOAD_DELAY_DEFAULT;
    }


    class MacroConfig {
        public static final int COUNT_DEFAULT = 100;
        public static final int COUNT_MAX = 10000;
        public static final int COUNT_MIN = 1;

        public static final int CAPTURE_DELAY_DEFAULT = 500;
        public static final int CAPTURE_DELAY_MAX = 5000;
        public static final int CAPTURE_DELAY_MIN = 200;

        public static final int CAPTURE_DURATION_DEFAULT = 50;
        public static final int CAPTURE_DURATION_MAX = 1000;
        public static final int CAPTURE_DURATION_MIN = 0;

        public static final int KEY_INPUT_DURATION_DEFAULT = 40;
        public static final int KEY_INPUT_DURATION_MAX = 100;
        public static final int KEY_INPUT_DURATION_MIN = 20;

        @Expose
        public AnalysisKey analysisKey = AnalysisKey.F11;

        @Expose
        public int count = COUNT_DEFAULT;

        @Expose
        public int captureDelay = CAPTURE_DELAY_DEFAULT;

        @Expose
        public int captureDuration = CAPTURE_DURATION_DEFAULT;

        @Expose
        public int keyInputDuration = KEY_INPUT_DURATION_DEFAULT;
    }
}
