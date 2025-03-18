package com.github.johypark97.varchivemacro.macro.model;

import com.google.gson.annotations.Expose;
import java.nio.file.Path;
import java.util.Set;

public class ScannerConfig {
    public static final Path ACCOUNT_FILE_DEFAULT = Path.of("account.txt");
    public static final Path CACHE_DIRECTORY_DEFAULT = Path.of("cache");

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

    @Expose
    public Set<String> selectedCategorySet = Set.of();

    @Expose
    public Path cacheDirectory = CACHE_DIRECTORY_DEFAULT;

    @Expose
    public int captureDelay = CAPTURE_DELAY_DEFAULT;

    @Expose
    public int keyInputDuration = KEY_INPUT_DURATION_DEFAULT;

    @Expose
    public int analysisThreadCount = ANALYSIS_THREAD_COUNT_DEFAULT;

    @Expose
    public Path accountFile = ACCOUNT_FILE_DEFAULT;

    @Expose
    public int recordUploadDelay = RECORD_UPLOAD_DELAY_DEFAULT;
}
