package com.github.johypark97.varchivemacro.macro.common.config.infra.service;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.infra.model.ConfigJson;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigLoadService {
    private final ConfigRepository configRepository;

    public ConfigLoadService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public boolean load(Path configFilePath) throws IOException {
        if (!Files.exists(configFilePath)) {
            return false;
        }

        ConfigJson configJson;
        try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
            Gson gson = GsonWrapper.newGsonBuilder_dump().create();
            configJson = gson.fromJson(reader, ConfigJson.class);
        }

        configRepository.saveMacroConfig(configJson.macroConfig);
        configRepository.saveScannerConfig(configJson.scannerConfig);

        return true;
    }
}
