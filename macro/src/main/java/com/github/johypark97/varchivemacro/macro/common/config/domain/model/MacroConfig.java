package com.github.johypark97.varchivemacro.macro.common.config.domain.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.google.gson.annotations.Expose;

public record MacroConfig(
        // @formatter:off
        @Expose InputKeyCombination startDownKey,
        @Expose InputKeyCombination startUpKey,
        @Expose InputKeyCombination stopKey,
        @Expose InputKeyCombination uploadKey,
        @Expose MacroClientMode clientMode,
        @Expose int count,
        @Expose int keyHoldTime,
        @Expose int postCaptureDelay,
        @Expose int songSwitchingTime
        // @formatter:on
) {
    public static final InputKeyCombination START_DOWN_KEY_DEFAULT =
            new InputKeyCombination(InputKey.CLOSE_BRACKET, false, true, false);

    public static final InputKeyCombination START_UP_KEY_DEFAULT =
            new InputKeyCombination(InputKey.OPEN_BRACKET, false, true, false);

    public static final InputKeyCombination STOP_KEY_DEFAULT =
            InputKeyCombination.from(InputKey.DELETE);

    public static final InputKeyCombination UPLOAD_KEY_DEFAULT =
            InputKeyCombination.from(InputKey.INSERT);

    public static final MacroClientMode CLIENT_MODE_DEFAULT = MacroClientMode.SEPARATELY;

    public static final int COUNT_DEFAULT = 100;
    public static final int COUNT_MAX = 10000;
    public static final int COUNT_MIN = 1;

    public static final int SONG_SWITCHING_TIME_DEFAULT = 500;
    public static final int SONG_SWITCHING_TIME_MAX = 5000;
    public static final int SONG_SWITCHING_TIME_MIN = 200;

    public static final int POST_CAPTURE_DELAY_DEFAULT = 50;
    public static final int POST_CAPTURE_DELAY_MAX = 1000;
    public static final int POST_CAPTURE_DELAY_MIN = 0;

    public static final int KEY_HOLD_TIME_DEFAULT = 40;
    public static final int KEY_HOLD_TIME_MAX = 100;
    public static final int KEY_HOLD_TIME_MIN = 20;

    public Builder toBuilder() {
        Builder builder = new Builder();

        builder.clientMode = clientMode;
        builder.count = count;
        builder.keyHoldTime = keyHoldTime;
        builder.postCaptureDelay = postCaptureDelay;
        builder.songSwitchingTime = songSwitchingTime;
        builder.startDownKey = startDownKey;
        builder.startUpKey = startUpKey;
        builder.stopKey = stopKey;
        builder.uploadKey = uploadKey;

        return builder;
    }

    public static class Builder {
        public InputKeyCombination startDownKey = START_DOWN_KEY_DEFAULT;
        public InputKeyCombination startUpKey = START_UP_KEY_DEFAULT;
        public InputKeyCombination stopKey = STOP_KEY_DEFAULT;
        public InputKeyCombination uploadKey = UPLOAD_KEY_DEFAULT;
        public MacroClientMode clientMode = CLIENT_MODE_DEFAULT;
        public int count = COUNT_DEFAULT;
        public int keyHoldTime = KEY_HOLD_TIME_DEFAULT;
        public int postCaptureDelay = POST_CAPTURE_DELAY_DEFAULT;
        public int songSwitchingTime = SONG_SWITCHING_TIME_DEFAULT;

        public MacroConfig build() {
            return new MacroConfig(startDownKey, startUpKey, stopKey, uploadKey, clientMode, count,
                    keyHoldTime, postCaptureDelay, songSwitchingTime);
        }
    }
}
