package com.github.johypark97.varchivemacro.macro.model;

import com.google.gson.annotations.Expose;

public class MacroConfig {
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
