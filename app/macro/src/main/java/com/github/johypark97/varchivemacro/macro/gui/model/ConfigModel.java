package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.core.clientmacro.AnalyzeKey;
import com.github.johypark97.varchivemacro.macro.gui.model.datastruct.ConfigData;
import com.github.johypark97.varchivemacro.macro.gui.presenter.MacroViewConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ConfigModel implements MacroViewConfig {
    private static final Path CONFIG_PATH = Path.of("config.json");

    private ConfigData data = new ConfigData();

    public void load() throws IOException {
        if (Files.exists(CONFIG_PATH)) {
            ConfigData data = ConfigData.load(CONFIG_PATH);
            if (data != null) {
                this.data = data;
            }
        }
    }

    public void save() throws IOException {
        if (data != null) {
            data.save(CONFIG_PATH);
        }
    }

    @Override
    public Path getAccountPath() {
        if (data.accountPath == null) {
            data.accountPath = new ConfigData().accountPath;
        }

        return data.accountPath;
    }

    @Override
    public void setAccountPath(Path path) {
        data.accountPath = path;
    }

    @Override
    public Path getCacheDir() {
        if (data.cacheDir == null) {
            data.cacheDir = new ConfigData().cacheDir;
        }

        return data.cacheDir;
    }

    @Override
    public void setCacheDir(Path path) {
        data.cacheDir = path;
    }

    @Override
    public Set<String> getSelectedDlcTabs() {
        if (data.selectedDlcTabs == null) {
            data.selectedDlcTabs = new ConfigData().selectedDlcTabs;
        }

        return data.selectedDlcTabs;
    }

    @Override
    public void setSelectedDlcTabs(Set<String> tabs) {
        data.selectedDlcTabs = tabs;
    }

    @Override
    public int getRecordUploadDelay() {
        if (data.recordUploadDelay == null) {
            data.recordUploadDelay = RECORD_UPLOAD_DELAY;
        }

        return data.recordUploadDelay;
    }

    @Override
    public void setRecordUploadDelay(int value) {
        data.recordUploadDelay = value;
    }

    @Override
    public int getScannerCaptureDelay() {
        if (data.scannerCaptureDelay == null) {
            data.scannerCaptureDelay = SCANNER_CAPTURE_DELAY;
        }

        return data.scannerCaptureDelay;
    }

    @Override
    public void setScannerCaptureDelay(int value) {
        data.scannerCaptureDelay = value;
    }

    @Override
    public int getScannerKeyInputDuration() {
        if (data.scannerKeyInputDuration == null) {
            data.scannerKeyInputDuration = KEY_INPUT_DURATION;
        }

        return data.scannerKeyInputDuration;
    }

    @Override
    public void setScannerKeyInputDuration(int value) {
        data.scannerKeyInputDuration = value;
    }

    @Override
    public AnalyzeKey getMacroAnalyzeKey() {
        AnalyzeKey defaultValue = AnalyzeKey.F11;

        if (data.macroAnalyzeKey == null) {
            return defaultValue;
        }

        try {
            return AnalyzeKey.valueOf(data.macroAnalyzeKey);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    @Override
    public void setMacroAnalyzeKey(AnalyzeKey value) {
        data.macroAnalyzeKey = value.toString();
    }

    @Override
    public int getMacroCount() {
        if (data.macroCount == null) {
            data.macroCount = MACRO_COUNT;
        }

        return data.macroCount;
    }

    @Override
    public void setMacroCount(int value) {
        data.macroCount = value;
    }

    @Override
    public int getMacroCaptureDelay() {
        if (data.macroCaptureDelay == null) {
            data.macroCaptureDelay = MACRO_CAPTURE_DELAY;
        }

        return data.macroCaptureDelay;
    }

    @Override
    public void setMacroCaptureDelay(int value) {
        data.macroCaptureDelay = value;
    }

    @Override
    public int getMacroCaptureDuration() {
        if (data.macroCaptureDuration == null) {
            data.macroCaptureDuration = MACRO_CAPTURE_DURATION;
        }

        return data.macroCaptureDuration;
    }

    @Override
    public void setMacroCaptureDuration(int value) {
        data.macroCaptureDuration = value;
    }

    @Override
    public int getMacroKeyInputDuration() {
        if (data.macroKeyInputDuration == null) {
            data.macroKeyInputDuration = KEY_INPUT_DURATION;
        }

        return data.macroKeyInputDuration;
    }

    @Override
    public void setMacroKeyInputDuration(int value) {
        data.macroKeyInputDuration = value;
    }
}
