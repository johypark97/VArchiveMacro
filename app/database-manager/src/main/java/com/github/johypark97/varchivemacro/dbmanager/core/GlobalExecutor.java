package com.github.johypark97.varchivemacro.dbmanager.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class GlobalExecutor {
    private ExecutorService executorService;

    private GlobalExecutor() {
    }

    public static GlobalExecutor getInstance() {
        return GlobalExecutorSingleton.INSTANCE;
    }

    public synchronized boolean isIdle() {
        return executorService == null || executorService.isTerminated();
    }

    public synchronized boolean shutdownNow() {
        if (isIdle()) {
            return false;
        }

        executorService.shutdownNow();

        return true;
    }

    public synchronized boolean use(Consumer<ExecutorService> consumer) {
        if (!isIdle()) {
            return false;
        }

        executorService = Executors.newSingleThreadExecutor();
        consumer.accept(executorService);
        executorService.shutdown();

        return true;
    }

    private static class GlobalExecutorSingleton {
        private static final GlobalExecutor INSTANCE = new GlobalExecutor();
    }
}
