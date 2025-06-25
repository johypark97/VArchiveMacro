package com.github.johypark97.varchivemacro.macro.integration.app.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.model.MacroProgress;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.task.AbstractMacroTask;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.task.AtOnceMacroTask;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.task.SeparatelyMacroTask;
import javafx.concurrent.Task;

public class DefaultMacroService implements MacroService {
    private final ConfigRepository configRepository;

    public DefaultMacroService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Task<MacroProgress> createMacroTask(MacroDirection direction) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        MacroConfig config = configRepository.findMacroConfig();

        AbstractMacroTask task = switch (config.clientMode()) {
            case AT_ONCE -> new AtOnceMacroTask(config, direction);
            case SEPARATELY -> new SeparatelyMacroTask(config, direction);
        };

        return TaskManager.getInstance().register(AbstractMacroTask.class, task);
    }

    @Override
    public boolean stopMacroTask() {
        return TaskManager.Helper.cancel(AbstractMacroTask.class);
    }
}
