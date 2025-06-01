package com.github.johypark97.varchivemacro.macro.application.macro.service;

import com.github.johypark97.varchivemacro.macro.application.macro.model.MacroDirection;
import javafx.concurrent.Task;

public interface MacroService {
    Task<Void> createMacroTask(MacroDirection direction);

    boolean stopMacroTask();
}
