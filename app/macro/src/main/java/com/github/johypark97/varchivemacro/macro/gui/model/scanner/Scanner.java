package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.command.Command;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.table.TableModel;

public class Scanner {
    private final ScannerTaskManager taskManager = new ScannerTaskManager();

    public Consumer<Exception> whenThrown;
    public Runnable whenCanceled;
    public Runnable whenCaptureDone;
    public Runnable whenDone;
    public Runnable whenStart_capture;
    public Runnable whenStart_loadImages;

    public Command getCommand_scan(Map<String, List<LocalSong>> tabSongMap) {
        return createCommand_scan(tabSongMap);
    }

    public Command getCommand_loadCapturedImages(Map<String, List<LocalSong>> tabSongMap) {
        return createCommand_loadCapturedImages(tabSongMap);
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

    protected Command createCommand_scan(Map<String, List<LocalSong>> tabSongMap) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_capture.run();

                taskManager.clear();

                CaptureService captureService = new CaptureService();
                captureService.execute(taskManager, tabSongMap);

                try {
                    captureService.awaitCapture();
                } catch (InterruptedException e) {
                    captureService.shutdownNow();
                    whenCanceled.run();
                    return false;
                }

                if (captureService.exception != null) {
                    whenThrown.accept(captureService.exception);
                    return false;
                }

                whenCaptureDone.run();

                try {
                    captureService.await();
                } catch (InterruptedException e) {
                    captureService.shutdownNow();
                    whenCanceled.run();
                    return false;
                }

                whenDone.run();
                return true;
            }
        };
    }

    protected Command createCommand_loadCapturedImages(Map<String, List<LocalSong>> tabSongMap) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_loadImages.run();

                taskManager.clear();
                tabSongMap.values().forEach((songs) -> songs.forEach((song) -> {
                    ScannerTask task = taskManager.create(song);
                    if (!Files.exists(task.getFilePath())) {
                        task.setException(new IOException("File not found"));
                    }
                }));

                whenDone.run();
                return true;
            }
        };
    }
}
