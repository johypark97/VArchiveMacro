package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.google.gson.annotations.Expose;
import java.util.Set;

public record ScannerConfig(@Expose Set<String> selectedCategory, @Expose String cacheDirectory,
                            @Expose int captureDelay, @Expose int keyInputDuration,
                            @Expose int analysisThreadCount, @Expose String accountFile,
                            @Expose int recordUploadDelay) {
    public static final String ACCOUNT_FILE_DEFAULT = "account.txt";
    public static final String CACHE_DIRECTORY_DEFAULT = "cache";

    public static final int CAPTURE_DELAY_DEFAULT = 0;
    public static final int CAPTURE_DELAY_MAX = 1000;
    public static final int CAPTURE_DELAY_MIN = 0;

    public static final int KEY_INPUT_DURATION_DEFAULT = 40;
    public static final int KEY_INPUT_DURATION_MAX = 100;
    public static final int KEY_INPUT_DURATION_MIN = 20;

    public static final int ANALYSIS_THREAD_COUNT_DEFAULT =
            Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
    public static final int ANALYSIS_THREAD_COUNT_MAX = Runtime.getRuntime().availableProcessors();

    public static final int RECORD_UPLOAD_DELAY_DEFAULT = 40;
    public static final int RECORD_UPLOAD_DELAY_MAX = 1000;
    public static final int RECORD_UPLOAD_DELAY_MIN = 0;

    public static ScannerConfig createDefault() {
        return new ScannerConfig(Set.of(), CACHE_DIRECTORY_DEFAULT, CAPTURE_DELAY_DEFAULT,
                KEY_INPUT_DURATION_DEFAULT, ANALYSIS_THREAD_COUNT_DEFAULT, ACCOUNT_FILE_DEFAULT,
                RECORD_UPLOAD_DELAY_DEFAULT);
    }
}
