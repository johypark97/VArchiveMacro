package com.github.johypark97.varchivemacro.macro.core.command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandRunner {
    private ExecutorService commandExecutor;

    public synchronized boolean isRunning() {
        return commandExecutor != null && !commandExecutor.isTerminated();
    }

    public synchronized boolean start(Command command) {
        if (isRunning()) {
            return false;
        }

        commandExecutor = Executors.newSingleThreadExecutor();
        commandExecutor.execute(() -> {
            Command p = command;
            while (p.run() && p.getNext() != null) {
                p = p.getNext();
            }
        });
        commandExecutor.shutdown();

        return true;
    }

    public synchronized boolean stop() {
        if (!isRunning()) {
            return false;
        }

        commandExecutor.shutdownNow();
        return true;
    }
}
