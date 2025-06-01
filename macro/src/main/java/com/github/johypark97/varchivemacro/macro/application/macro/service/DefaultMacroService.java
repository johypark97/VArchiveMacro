package com.github.johypark97.varchivemacro.macro.application.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.application.macro.task.AbstractMacroTask;
import com.github.johypark97.varchivemacro.macro.application.macro.task.AtOnceMacroTask;
import com.github.johypark97.varchivemacro.macro.application.macro.task.SeparatelyMacroTask;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import javafx.concurrent.Task;

public class DefaultMacroService implements MacroService {
    private final ConfigRepository configRepository;

    public DefaultMacroService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Task<Void> createMacroTask(MacroDirection direction) {
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
