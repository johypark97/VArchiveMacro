package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.command.Command;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.Status;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionArea.Button;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionArea.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionAreaFactory;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
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
    public Runnable whenStart_analyze;
    public Runnable whenStart_capture;
    public Runnable whenStart_loadImages;

    public Command getCommand_scan(Map<String, List<LocalSong>> tabSongMap) {
        Command root = createCommand_scan(tabSongMap);
        root.setNext(createCommand_analyze());
        return root;
    }

    public Command getCommand_loadCapturedImages(Map<String, List<LocalSong>> tabSongMap) {
        return createCommand_loadCapturedImages(tabSongMap);
    }

    public Command getCommand_analyze() {
        return createCommand_analyze();
    }

    public TableModel getTaskTableModel() {
        return taskManager.tableModel;
    }

    public CollectionTaskData getTaskData(int taskNumber) throws Exception {
        ScannerTask task = taskManager.getTask(taskNumber);
        if (task == null) {
            return null;
        }

        CollectionTaskData data = new CollectionTaskData();

        BufferedImage image = task.loadImage();
        data.fullImage = image;

        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(size);
        data.titleImage = area.getTitle(image);

        area.keys().forEach((cell) -> {
            Button button = cell.getRowKey();
            Pattern pattern = cell.getColumnKey();
            String key = cell.getValue();

            AnalyzedData analyzedData = task.getAnalyzedData(button, pattern);
            if (analyzedData != null) {
                RecordData recordData = new RecordData();
                recordData.maxCombo = analyzedData.isMaxCombo;
                recordData.maxComboImage = area.getComboMark(image, button, pattern);
                recordData.rate = analyzedData.rateText;
                recordData.rateImage = area.getRate(image, button, pattern);

                data.addRecord(key, recordData);
            }
        });

        return data;
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
                    if (Files.exists(task.getFilePath())) {
                        task.setStatus(Status.CACHED);
                    } else {
                        task.setException(new IOException("File not found"));
                    }
                }));

                whenDone.run();
                return true;
            }
        };
    }

    protected Command createCommand_analyze() {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_analyze.run();

                AnalysisService analysisService = new AnalysisService();
                analysisService.execute(taskManager);

                try {
                    analysisService.await();
                } catch (InterruptedException e) {
                    analysisService.shutdownNow();
                    whenCanceled.run();
                    return false;
                }

                whenDone.run();
                return true;
            }
        };
    }
}
