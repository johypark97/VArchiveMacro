package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface ConfigModel {
    boolean load() throws IOException;

    void save() throws IOException;

    ScannerConfig getScannerConfig();

    void setScannerConfig(ScannerConfig value);

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
}
