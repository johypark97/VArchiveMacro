package com.github.johypark97.varchivemacro.macro.fxgui.model.service;

import java.util.function.Supplier;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class MacroService extends Service<Void> {
    private Supplier<? extends Task<Void>> taskConstructor;

    public void setTaskConstructor(Supplier<? extends Task<Void>> taskConstructor) {
        this.taskConstructor = taskConstructor;
    }

    @Override
    protected Task<Void> createTask() {
        return taskConstructor.get();
    }
}
