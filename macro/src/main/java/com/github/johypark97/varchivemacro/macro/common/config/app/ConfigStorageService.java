package com.github.johypark97.varchivemacro.macro.common.config.app;

import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.infra.service.ConfigLoadService;
import com.github.johypark97.varchivemacro.macro.common.config.infra.service.ConfigSaveService;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigStorageService {
    private final ConfigRepository configRepository;
    private final Path configFilePath;

    public ConfigStorageService(ConfigRepository configRepository, Path configFilePath) {
        this.configFilePath = configFilePath;
        this.configRepository = configRepository;
    }

    public boolean load() throws IOException {
        return new ConfigLoadService(configRepository).load(configFilePath);
    }

    public void save() throws IOException {
        new ConfigSaveService(configRepository).save(configFilePath);
    }
}
