package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.table.TableModel;

public class Scanner {
    private final CaptureTaskManager taskManager = new CaptureTaskManager();

    private final Consumer<Exception> whenThrown;
    private final Runnable whenCanceled;
    private final Runnable whenCaptureDone;
    private final Runnable whenDone;

    private ExecutorService controlExecutor;

    public Scanner(Runnable whenCaptureDone, Runnable whenDone, Runnable whenCanceled,
            Consumer<Exception> whenThrown) {
        this.whenCanceled = whenCanceled;
        this.whenCaptureDone = whenCaptureDone;
        this.whenDone = whenDone;
        this.whenThrown = whenThrown;
    }

    public synchronized boolean isRunning() {
        return controlExecutor != null && !controlExecutor.isTerminated();
    }

    public synchronized boolean startScanning(Map<String, List<LocalSong>> tabSongMap) {
        if (isRunning()) {
            return false;
        }

        taskManager.clear();

        run(() -> {
            CaptureService captureService = new CaptureService(taskManager::create);
            captureService.execute(tabSongMap);

            try {
                captureService.awaitCapture();
            } catch (InterruptedException e) {
                captureService.shutdownNow();
                whenCanceled.run();
                return;
            }

            if (captureService.exception != null) {
                whenThrown.accept(captureService.exception);
                return;
            }

            whenCaptureDone.run();

            try {
                captureService.await();
            } catch (InterruptedException e) {
                captureService.shutdownNow();
                whenCanceled.run();
                return;
            }

            whenDone.run();
        });

        return true;
    }

    public synchronized boolean saveImagesToDisk() {
        if (isRunning()) {
            return false;
        }

        run(() -> {
            try {
                taskManager.saveToDisk();
            } catch (IOException e) {
                whenThrown.accept(e);
                return;
            }

            whenDone.run();
        });

        return true;
    }

    public synchronized boolean loadImagesFromDisk(Map<String, List<LocalSong>> tabSongMap) {
        if (isRunning()) {
            return false;
        }

        taskManager.clear();
        tabSongMap.values().forEach((songs) -> songs.forEach(taskManager::create));

        run(() -> {
            try {
                taskManager.loadFromDisk();
            } catch (IOException e) {
                whenThrown.accept(e);
                return;
            }

            whenDone.run();
        });

        return true;
    }

    public synchronized boolean stop() {
        if (!isRunning()) {
            return false;
        }

        controlExecutor.shutdownNow();
        return true;
    }

    private synchronized void run(Runnable task) {
        if (!isRunning()) {
            controlExecutor = Executors.newSingleThreadExecutor();
            controlExecutor.submit(task);
            controlExecutor.shutdown();
        }
    }

    public TableModel getTaskTableModel() {
        return taskManager.tableModel;
    }

    public CollectionTaskData getTaskData(int taskNumber) {
        CaptureTask task = taskManager.getTask(taskNumber);
        if (task == null) {
            return null;
        }

        return AnalysisService.analyze(task);
    }
}
