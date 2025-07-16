package com.github.johypark97.varchivemacro.macro.integration.app.macro;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import javafx.concurrent.Task;

public class MacroService {
    private final ConfigService configService;

    public MacroService(ConfigService configService) {
        this.configService = configService;
    }

    public Task<MacroProgress> createMacroTask(MacroDirection direction) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        MacroConfig config = configService.findMacroConfig();

        AbstractMacroTask task = switch (config.clientMode()) {
            case AT_ONCE -> new AtOnceMacroTask(config, direction);
            case SEPARATELY -> new SeparatelyMacroTask(config, direction);
        };

        return TaskManager.getInstance().register(AbstractMacroTask.class, task);
    }

    public boolean stopMacroTask() {
        return TaskManager.Helper.cancel(AbstractMacroTask.class);
    }
}
