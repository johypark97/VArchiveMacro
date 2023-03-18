package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.macro.gui.model.datastruct.ConfigData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ConfigModel {
    private static final Path CONFIG_PATH = Path.of("config.json");

    private ConfigData data = new ConfigData();

    public Path getAccountPath() {
        if (data.accountPath == null) {
            data.accountPath = new ConfigData().accountPath;
        }

        return data.accountPath;
    }

    public void setAccountPath(Path path) {
        data.accountPath = path;
    }

    public Path getCacheDir() {
        if (data.cacheDir == null) {
            data.cacheDir = new ConfigData().cacheDir;
        }

        return data.cacheDir;
    }

    public void setCacheDir(Path path) {
        data.cacheDir = path;
    }

    public Set<String> getSelectedDlcTabs() {
        if (data.selectedDlcTabs == null) {
            data.selectedDlcTabs = new ConfigData().selectedDlcTabs;
        }

        return data.selectedDlcTabs;
    }

    public void setSelectedDlcTabs(Set<String> tabs) {
        data.selectedDlcTabs = tabs;
    }

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
}
