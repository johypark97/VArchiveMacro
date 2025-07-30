package com.github.johypark97.varchivemacro.macro.common.config.domain.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.google.gson.annotations.Expose;
import java.util.Set;

public record ScannerConfig(
        // @formatter:off
        @Expose InputKeyCombination startKey,
        @Expose InputKeyCombination stopKey,
        @Expose Set<String> selectedCategory,
        @Expose String accountFile,
        @Expose String cacheDirectory,
        @Expose boolean autoAnalysis,
        @Expose int analyzerThreadCount,
        @Expose int captureDelay,
        @Expose int keyHoldTime
        // @formatter:on
) {
    public static final InputKeyCombination START_KEY_DEFAULT =
            new InputKeyCombination(InputKey.ENTER, true, false, false);

    public static final InputKeyCombination STOP_KEY_DEFAULT =
            InputKeyCombination.from(InputKey.BACK_SPACE);

    public static final Set<String> SELECTED_CATEGORY_DEFAULT = Set.of();

    public static final String ACCOUNT_FILE_DEFAULT = "account.txt";

    public static final String CACHE_DIRECTORY_DEFAULT = "cache";

    public static final boolean AUTO_ANALYSIS_DEFAULT = false;

    public static final int ANALYZER_THREAD_COUNT_DEFAULT;
    public static final int ANALYZER_THREAD_COUNT_MAX;
    public static final int ANALYZER_THREAD_COUNT_MIN = 1;

    public static final int CAPTURE_DELAY_DEFAULT = 0;
    public static final int CAPTURE_DELAY_MAX = 1000;
    public static final int CAPTURE_DELAY_MIN = 0;

    public static final int KEY_HOLD_TIME_DEFAULT = 40;
    public static final int KEY_HOLD_TIME_MAX = 100;
    public static final int KEY_HOLD_TIME_MIN = 20;

    static {
        ANALYZER_THREAD_COUNT_MAX = Runtime.getRuntime().availableProcessors();
        ANALYZER_THREAD_COUNT_DEFAULT =
                Math.max(ANALYZER_THREAD_COUNT_MIN, ANALYZER_THREAD_COUNT_MAX / 4);
    }

    public Builder toBuilder() {
        Builder builder = new Builder();

        builder.accountFile = accountFile;
        builder.analyzerThreadCount = analyzerThreadCount;
        builder.autoAnalysis = autoAnalysis;
        builder.cacheDirectory = cacheDirectory;
        builder.captureDelay = captureDelay;
        builder.keyHoldTime = keyHoldTime;
        builder.selectedCategory = selectedCategory;
        builder.startKey = startKey;
        builder.stopKey = stopKey;

        return builder;
    }

    public static class Builder {
        public InputKeyCombination startKey = START_KEY_DEFAULT;
        public InputKeyCombination stopKey = STOP_KEY_DEFAULT;
        public Set<String> selectedCategory = SELECTED_CATEGORY_DEFAULT;
        public String accountFile = ACCOUNT_FILE_DEFAULT;
        public String cacheDirectory = CACHE_DIRECTORY_DEFAULT;
        public boolean autoAnalysis = AUTO_ANALYSIS_DEFAULT;
        public int analyzerThreadCount = ANALYZER_THREAD_COUNT_DEFAULT;
        public int captureDelay = CAPTURE_DELAY_DEFAULT;
        public int keyHoldTime = KEY_HOLD_TIME_DEFAULT;

        public ScannerConfig build() {
            return new ScannerConfig(startKey, stopKey, selectedCategory, accountFile,
                    cacheDirectory, autoAnalysis, analyzerThreadCount, captureDelay, keyHoldTime);
        }
    }
}
