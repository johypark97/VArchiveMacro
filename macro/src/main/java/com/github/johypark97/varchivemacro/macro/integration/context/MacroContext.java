package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.config.AppConfigManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.MacroService;

public class MacroContext implements Context {
    public final MacroService macroService;

    public MacroContext() {
        AppConfigService appConfigService = AppConfigManager.INSTANCE.getAppConfigService();

        macroService = new MacroService(appConfigService);
    }
}
