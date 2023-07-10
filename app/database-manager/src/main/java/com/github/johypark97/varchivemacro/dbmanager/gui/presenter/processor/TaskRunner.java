package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.processor;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class TaskRunner {
    private final Function<Throwable, Void> whenThrown;
    private final Runnable whenDone;

    private ExecutorService executor;

    public TaskRunner(Function<Throwable, Void> whenThrown, Runnable whenDone) {
        this.whenDone = whenDone;
        this.whenThrown = whenThrown;
    }

    public synchronized boolean isRunning() {
        return executor != null && !executor.isTerminated();
    }

    public synchronized boolean run(Callable<Void> task) {
        if (isRunning()) {
            return false;
        }

        executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(task);
        executor.shutdown();

        CompletableFuture.runAsync(() -> {
            try {
                future.get();
                whenDone.run();
            } catch (InterruptedException ignored) {
            } catch (ExecutionException e) {
                whenThrown.apply(e);
            }
        });

        return true;
    }

    public synchronized boolean cancel() {
        if (!isRunning()) {
            return false;
        }

        executor.shutdownNow();
        return true;
    }
}
