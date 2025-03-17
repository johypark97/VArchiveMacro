package com.github.johypark97.varchivemacro.macro.service.task;

import java.lang.ref.WeakReference;
import javafx.concurrent.Task;

public abstract class InterruptibleTask<T> extends Task<T> {
    private WeakReference<Thread> threadReference;
    private boolean requestCancel;

    protected abstract T callTask() throws Exception;

    @Override
    protected final T call() throws Exception {
        threadReference = new WeakReference<>(Thread.currentThread());

        T value = callTask();

        if (requestCancel) {
            super.cancel(true);
        }

        return value;
    }

    @Override
    public final boolean cancel(boolean mayInterruptIfRunning) {
        if (threadReference == null) {
            return false;
        }

        Thread thread = threadReference.get();
        if (thread == null) {
            return false;
        }

        requestCancel = true;

        if (mayInterruptIfRunning) {
            thread.interrupt();
        }

        return true;
    }
}
