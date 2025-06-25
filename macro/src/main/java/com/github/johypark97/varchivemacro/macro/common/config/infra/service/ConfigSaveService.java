package com.github.johypark97.varchivemacro.macro.common.config.infra.service;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.infra.model.ConfigJson;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigSaveService {
    private final ConfigRepository configRepository;

    public ConfigSaveService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public void save(Path configFilePath) throws IOException {
        ConfigJson configJson = new ConfigJson();

        configJson.macroConfig = configRepository.findMacroConfig();
        configJson.scannerConfig = configRepository.findScannerConfig();

        Gson gson = GsonWrapper.newGsonBuilder_dump().create();
        Files.writeString(configFilePath, gson.toJson(configJson));
    }
}
