package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.swing.table.TableModel;

public class Scanner {
    private final ScanDataManager dataManager = new ScanDataManager();

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

        dataManager.clear();
        run(() -> {
            CaptureService captureService = new CaptureService();
            captureService.execute(tabSongMap, dataManager::add);

            try {
                captureService.awaitCapture();
                whenCaptureDone.run();
                captureService.await();
            } catch (InterruptedException e) {
                captureService.shutdownNow();
                whenCanceled.run();
                return;
            }

            if (captureService.exception != null) {
                whenThrown.accept(captureService.exception);
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
        return dataManager.tableModel;
    }
}
