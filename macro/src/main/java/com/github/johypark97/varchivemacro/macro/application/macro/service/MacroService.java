package com.github.johypark97.varchivemacro.macro.application.macro.service;

import javafx.concurrent.Task;
import javafx.geometry.VerticalDirection;

public interface MacroService {
    Task<Void> createMacroTask(VerticalDirection direction);

    void stopMacroTask();
}
