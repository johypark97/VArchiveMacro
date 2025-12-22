package com.github.johypark97.varchivemacro.macro.common.config.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.model.value.RangedSettingValue;
import com.github.johypark97.varchivemacro.macro.common.config.model.value.SettingValue;

public record MacroConfig(SettingValue<InputKeyCombination> startDownKey,
                          SettingValue<InputKeyCombination> startUpKey,
                          SettingValue<InputKeyCombination> stopKey,
                          SettingValue<InputKeyCombination> uploadKey,
                          SettingValue<MacroClientMode> clientMode,
                          RangedSettingValue<Integer> count,
                          RangedSettingValue<Integer> keyHoldTime,
                          RangedSettingValue<Integer> postCaptureDelay,
                          RangedSettingValue<Integer> songSwitchingTime)
        implements ConfigEditorModel.Config<MacroConfig, MacroConfig.Editor> {
    public static MacroConfig ofDefault() {
        return editDefault().commit();
    }

    public static MacroConfig.Editor editDefault() {
        return new Editor();
    }

    @Override
    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor extends ConfigEditorModel.Editor<MacroConfig, Editor> {
        @ConfigField
        private SettingValue<InputKeyCombination> startDownKey = SettingValue.of(
                new InputKeyCombination(InputKey.CLOSE_BRACKET, false, true, false));

        @ConfigField
        private SettingValue<InputKeyCombination> startUpKey =
                SettingValue.of(new InputKeyCombination(InputKey.OPEN_BRACKET, false, true, false));

        @ConfigField
        private SettingValue<InputKeyCombination> stopKey =
                SettingValue.of(InputKeyCombination.from(InputKey.BACK_SPACE));

        @ConfigField
        private SettingValue<InputKeyCombination> uploadKey =
                SettingValue.of(new InputKeyCombination(InputKey.INSERT, false, true, false));

        @ConfigField
        private SettingValue<MacroClientMode> clientMode =
                SettingValue.of(MacroClientMode.SEPARATELY);

        @ConfigField
        private RangedSettingValue<Integer> count = RangedSettingValue.of(100, 1, 10000);

        @ConfigField
        private RangedSettingValue<Integer> keyHoldTime = RangedSettingValue.of(40, 20, 100);

        @ConfigField
        private RangedSettingValue<Integer> postCaptureDelay = RangedSettingValue.of(50, 0, 1000);

        @ConfigField
        private RangedSettingValue<Integer> songSwitchingTime =
                RangedSettingValue.of(500, 200, 5000);

        private Editor() {
        }

        private Editor(MacroConfig config) {
            copyFrom(config);
        }

        public SettingValue<InputKeyCombination> getStartDownKey() {
            return startDownKey;
        }

        public Editor setStartDownKey(InputKeyCombination value) {
            startDownKey = startDownKey.with(value);
            return this;
        }

        public SettingValue<InputKeyCombination> getStartUpKey() {
            return startUpKey;
        }

        public Editor setStartUpKey(InputKeyCombination value) {
            startUpKey = startUpKey.with(value);
            return this;
        }

        public SettingValue<InputKeyCombination> getStopKey() {
            return stopKey;
        }

        public Editor setStopKey(InputKeyCombination value) {
            stopKey = stopKey.with(value);
            return this;
        }

        public SettingValue<InputKeyCombination> getUploadKey() {
            return uploadKey;
        }

        public Editor setUploadKey(InputKeyCombination value) {
            uploadKey = uploadKey.with(value);
            return this;
        }

        public SettingValue<MacroClientMode> getClientMode() {
            return clientMode;
        }

        public Editor setClientMode(MacroClientMode value) {
            clientMode = clientMode.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getCount() {
            return count;
        }

        public Editor setCount(int value) {
            count = count.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getKeyHoldTime() {
            return keyHoldTime;
        }

        public Editor setKeyHoldTime(int value) {
            keyHoldTime = keyHoldTime.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getPostCaptureDelay() {
            return postCaptureDelay;
        }

        public Editor setPostCaptureDelay(int value) {
            postCaptureDelay = postCaptureDelay.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getSongSwitchingTime() {
            return songSwitchingTime;
        }

        public Editor setSongSwitchingTime(int value) {
            songSwitchingTime = songSwitchingTime.with(value);
            return this;
        }

        @Override
        public MacroConfig commit() {
            return new MacroConfig(startDownKey, startUpKey, stopKey, uploadKey, clientMode, count,
                    keyHoldTime, postCaptureDelay, songSwitchingTime);
        }
    }
}
