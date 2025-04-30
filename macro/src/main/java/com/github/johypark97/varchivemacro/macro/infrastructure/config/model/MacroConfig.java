package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.google.gson.annotations.Expose;

public record MacroConfig(@Expose UploadKey uploadKey, @Expose int count, @Expose int captureDelay,
                          @Expose int captureDuration, @Expose int keyInputDuration) {
    public static final UploadKey UPLOAD_KEY_DEFAULT = UploadKey.F11;

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

    public static MacroConfig createDefault() {
        return new MacroConfig(UPLOAD_KEY_DEFAULT, COUNT_DEFAULT, CAPTURE_DELAY_DEFAULT,
                CAPTURE_DURATION_DEFAULT, KEY_INPUT_DURATION_DEFAULT);
    }
}
