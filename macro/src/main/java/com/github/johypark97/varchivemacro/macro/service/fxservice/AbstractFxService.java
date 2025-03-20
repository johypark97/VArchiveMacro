package com.github.johypark97.varchivemacro.macro.service.fxservice;

import java.util.function.Supplier;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public abstract class AbstractFxService extends Service<Void> {
    private Supplier<? extends Task<Void>> taskConstructor;

    public void setTaskConstructor(Supplier<? extends Task<Void>> taskConstructor) {
        this.taskConstructor = taskConstructor;
    }

    @Override
    protected Task<Void> createTask() {
        return taskConstructor.get();
    }
}
