package com.github.johypark97.varchivemacro.macro.common.config;

import com.github.johypark97.varchivemacro.macro.common.config.model.AppConfig;
import com.github.johypark97.varchivemacro.macro.common.config.service.DefaultConfigService;

public class DefaultAppConfigService extends DefaultConfigService<AppConfig, AppConfig.Editor>
        implements AppConfigService {
    public DefaultAppConfigService(AppConfigRepository appConfigRepository) {
        super(appConfigRepository, AppConfig::ofDefault);
    }
}
