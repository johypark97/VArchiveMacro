package com.github.johypark97.varchivemacro.macro.application.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.application.macro.task.MacroTask;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import javafx.concurrent.Task;

public class DefaultMacroService implements MacroService {
    @Override
    public Task<Void> createMacroTask(MacroConfig config, MacroDirection direction) {
        return TaskManager.getInstance().register(MacroTask.class,
                new MacroTask(config.uploadKey(), config.count(), config.captureDelay(),
                        config.captureDuration(), config.keyInputDuration(), direction));
    }

    @Override
    public boolean stopMacroTask() {
        return TaskManager.Helper.cancel(MacroTask.class);
    }
}
