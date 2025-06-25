package com.github.johypark97.varchivemacro.macro.integration.app.macro.service;

import com.github.johypark97.varchivemacro.macro.integration.app.macro.model.MacroDirection;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.model.MacroProgress;
import javafx.concurrent.Task;

public interface MacroService {
    Task<MacroProgress> createMacroTask(MacroDirection direction);

    boolean stopMacroTask();
}
