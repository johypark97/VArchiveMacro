package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.service.task.MacroTask;
import javafx.concurrent.Task;
import javafx.geometry.VerticalDirection;

public class DefaultMacroService implements MacroService {
    private final ConfigRepository configRepository;

    public DefaultMacroService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public Task<Void> createMacroTask(VerticalDirection direction) {
        MacroConfig config = configRepository.getMacroConfig();

        return TaskManager.getInstance().register(MacroTask.class,
                new MacroTask(config.analysisKey, config.count, config.captureDelay,
                        config.captureDuration, config.keyInputDuration, direction));
    }

    @Override
    public void stopMacroTask() {
        TaskManager.Helper.cancel(MacroTask.class);
    }
}
