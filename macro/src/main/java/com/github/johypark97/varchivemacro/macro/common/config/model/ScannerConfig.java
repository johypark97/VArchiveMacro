package com.github.johypark97.varchivemacro.macro.common.config.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.model.value.RangedSettingValue;
import com.github.johypark97.varchivemacro.macro.common.config.model.value.SettingValue;
import java.util.Set;

public record ScannerConfig(SettingValue<InputKeyCombination> startKey,
                            SettingValue<InputKeyCombination> stopKey,
                            SettingValue<Set<String>> selectedCategory,
                            SettingValue<String> accountFile,
                            SettingValue<String> cacheDirectory,
                            SettingValue<Boolean> autoAnalysis,
                            RangedSettingValue<Integer> analyzerThreadCount,
                            RangedSettingValue<Integer> captureDelay,
                            RangedSettingValue<Integer> keyHoldTime)
        implements ConfigEditorModel.Config<ScannerConfig, ScannerConfig.Editor> {
    public static ScannerConfig ofDefault() {
        return editDefault().commit();
    }

    public static ScannerConfig.Editor editDefault() {
        return new ScannerConfig.Editor();
    }

    @Override
    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor extends ConfigEditorModel.Editor<ScannerConfig, Editor> {
        @ConfigField
        private SettingValue<InputKeyCombination> startKey =
                SettingValue.of(new InputKeyCombination(InputKey.ENTER, true, false, false));

        @ConfigField
        private SettingValue<InputKeyCombination> stopKey =
                SettingValue.of(InputKeyCombination.from(InputKey.BACK_SPACE));

        @ConfigField
        private SettingValue<Set<String>> selectedCategory = SettingValue.of(Set.of());

        @ConfigField
        private SettingValue<String> accountFile = SettingValue.of("account.txt");

        @ConfigField
        private SettingValue<String> cacheDirectory = SettingValue.of("cache");

        @ConfigField
        private SettingValue<Boolean> autoAnalysis = SettingValue.of(false);

        @ConfigField
        private RangedSettingValue<Integer> analyzerThreadCount;

        @ConfigField
        private RangedSettingValue<Integer> captureDelay = RangedSettingValue.of(0, 0, 1000);

        @ConfigField
        private RangedSettingValue<Integer> keyHoldTime = RangedSettingValue.of(40, 20, 100);

        private Editor() {
            int min = 1;
            int max = Runtime.getRuntime().availableProcessors();
            int defaultValue = Math.max(min, max / 4);

            analyzerThreadCount = RangedSettingValue.of(defaultValue, min, max);
        }

        private Editor(ScannerConfig config) {
            copyFrom(config);
        }

        public SettingValue<InputKeyCombination> getStartKey() {
            return startKey;
        }

        public Editor setStartKey(InputKeyCombination value) {
            startKey = startKey.with(value);
            return this;
        }

        public SettingValue<InputKeyCombination> getStopKey() {
            return stopKey;
        }

        public Editor setStopKey(InputKeyCombination value) {
            stopKey = stopKey.with(value);
            return this;
        }

        public SettingValue<Set<String>> getSelectedCategory() {
            return selectedCategory;
        }

        public Editor setSelectedCategory(Set<String> value) {
            selectedCategory = selectedCategory.with(value);
            return this;
        }

        public SettingValue<String> getAccountFile() {
            return accountFile;
        }

        public Editor setAccountFile(String value) {
            accountFile = accountFile.with(value);
            return this;
        }

        public SettingValue<String> getCacheDirectory() {
            return cacheDirectory;
        }

        public Editor setCacheDirectory(String value) {
            cacheDirectory = cacheDirectory.with(value);
            return this;
        }

        public SettingValue<Boolean> getAutoAnalysis() {
            return autoAnalysis;
        }

        public Editor setAutoAnalysis(boolean value) {
            autoAnalysis = autoAnalysis.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getAnalyzerThreadCount() {
            return analyzerThreadCount;
        }

        public Editor setAnalyzerThreadCount(int value) {
            analyzerThreadCount = analyzerThreadCount.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getCaptureDelay() {
            return captureDelay;
        }

        public Editor setCaptureDelay(int value) {
            captureDelay = captureDelay.with(value);
            return this;
        }

        public RangedSettingValue<Integer> getKeyHoldTime() {
            return keyHoldTime;
        }

        public Editor setKeyHoldTime(int value) {
            keyHoldTime = keyHoldTime.with(value);
            return this;
        }

        @Override
        public ScannerConfig commit() {
            return new ScannerConfig(startKey, stopKey, selectedCategory, accountFile,
                    cacheDirectory, autoAnalysis, analyzerThreadCount, captureDelay, keyHoldTime);
        }
    }
}
