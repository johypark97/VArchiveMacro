package com.github.johypark97.varchivemacro.macro.gui.model;

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
    public int getScannerKeyInputDuration() {
        if (data.scannerKeyInputDuration == null) {
            data.scannerKeyInputDuration = SCANNER_KEY_INPUT_DURATION;
        }

        return data.scannerKeyInputDuration;
    }

    @Override
    public void setScannerKeyInputDuration(int value) {
        data.scannerKeyInputDuration = value;
    }
}
