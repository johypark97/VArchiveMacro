package com.github.johypark97.varchivemacro.macro.common.config;

import com.github.johypark97.varchivemacro.macro.common.config.model.AppConfig;
import com.github.johypark97.varchivemacro.macro.common.config.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.storage.JsonConfigStorage;
import java.nio.file.Path;

public class DefaultAppConfigRepository extends DefaultConfigRepository<AppConfig>
        implements AppConfigRepository {
    public DefaultAppConfigRepository(Path configFilePath) {
        super(new JsonConfigStorage(configFilePath));
    }
}
