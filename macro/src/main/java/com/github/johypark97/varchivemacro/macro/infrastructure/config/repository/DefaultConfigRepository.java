package com.github.johypark97.varchivemacro.macro.infrastructure.config.repository;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.AppConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultConfigRepository implements ConfigRepository {
    private final Path path;

    private AppConfig appConfig = new AppConfig();

    public DefaultConfigRepository(Path path) {
        this.path = path;
    }

    @Override
    public boolean load() throws IOException {
        if (!Files.exists(path)) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            Gson gson = GsonWrapper.newGsonBuilder_dump().create();
            appConfig = gson.fromJson(reader, AppConfig.class);
        }

        return true;
    }

    @Override
    public void flush() throws IOException {
        Gson gson = GsonWrapper.newGsonBuilder_dump().create();
        Files.writeString(path, gson.toJson(appConfig));
    }

    @Override
    public MacroConfig findMacroConfig() {
        return appConfig.macroConfig;
    }

    @Override
    public void saveMacroConfig(MacroConfig value) {
        appConfig.macroConfig = value;
    }

    @Override
    public ScannerConfig findScannerConfig() {
        return appConfig.scannerConfig;
    }

    @Override
    public void saveScannerConfig(ScannerConfig value) {
        appConfig.scannerConfig = value;
    }
}
