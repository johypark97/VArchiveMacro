package com.github.johypark97.varchivemacro.macro.application.macro.service;

import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import javafx.concurrent.Task;

public interface MacroService {
    Task<Void> createMacroTask(MacroConfig config, MacroDirection direction);

    boolean stopMacroTask();
}
