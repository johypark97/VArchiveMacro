package com.github.johypark97.varchivemacro.macro.integration.app.macro;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.model.MacroConfig;
import javafx.concurrent.Task;

public class MacroService {
    private final AppConfigService appConfigService;

    public MacroService(AppConfigService appConfigService) {
        this.appConfigService = appConfigService;
    }

    public Task<MacroProgress> createMacroTask(MacroDirection direction) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        MacroConfig config = appConfigService.getConfig().macroConfig();

        AbstractMacroTask task = switch (config.clientMode().value()) {
            case AT_ONCE -> new AtOnceMacroTask(config, direction);
            case SEPARATELY -> new SeparatelyMacroTask(config, direction);
        };

        return TaskManager.getInstance().register(AbstractMacroTask.class, task);
    }

    public boolean stopMacroTask() {
        return TaskManager.Helper.cancel(AbstractMacroTask.class);
    }
}
