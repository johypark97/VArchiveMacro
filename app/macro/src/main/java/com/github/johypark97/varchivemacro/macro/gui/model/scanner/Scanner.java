package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.command.Command;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.table.TableModel;

public class Scanner {
    private final ScannerTaskManager taskManager = new ScannerTaskManager();

    private final Consumer<Exception> whenThrown;
    private final Runnable whenCanceled;
    private final Runnable whenCaptureDone;
    private final Runnable whenDone;

    public Scanner(Runnable whenCaptureDone, Runnable whenDone, Runnable whenCanceled,
            Consumer<Exception> whenThrown) {
        this.whenCanceled = whenCanceled;
        this.whenCaptureDone = whenCaptureDone;
        this.whenDone = whenDone;
        this.whenThrown = whenThrown;
    }

    public Command getCommand_scan(Map<String, List<LocalSong>> tabSongMap) {
        return () -> {
            taskManager.clear();

            CaptureService captureService = new CaptureService();
            captureService.execute(taskManager, tabSongMap);

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
        };
    }

    public Command getCommand_loadCapturedImages(Map<String, List<LocalSong>> tabSongMap) {
        return () -> {
            taskManager.clear();
            tabSongMap.values().forEach((songs) -> songs.forEach((song) -> {
                ScannerTask task = taskManager.create(song);
                if (!Files.exists(task.getFilePath())) {
                    task.setException(new IOException("File not found"));
                }
            }));

            whenDone.run();
        };
    }

    public TableModel getTaskTableModel() {
        return taskManager.tableModel;
    }

    public CollectionTaskData getTaskData(int taskNumber) {
        ScannerTask task = taskManager.getTask(taskNumber);
        if (task == null) {
            return null;
        }

        return AnalysisService.analyze(task);
    }
}
