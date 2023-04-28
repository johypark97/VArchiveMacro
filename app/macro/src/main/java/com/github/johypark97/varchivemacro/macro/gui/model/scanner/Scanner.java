package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.Status;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionAreaFactory;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class Scanner {
    private final ResultManager resultManager = new ResultManager();
    private final ScannerTaskManager taskManager = new ScannerTaskManager();

    public Consumer<Exception> whenThrown;
    public Runnable whenCanceled;
    public Runnable whenCaptureDone;
    public Runnable whenDone;
    public Runnable whenStart_analyze;
    public Runnable whenStart_capture;
    public Runnable whenStart_collectResult;
    public Runnable whenStart_loadImages;
    public Runnable whenStart_uploadRecord;

    public void setModels(SongModel songModel, RecordModel recordModel) {
        resultManager.setModels(songModel, recordModel);
    }

    public Command getCommand_scan(Path cacheDir, int captureDelay, int inputDuration,
            Map<String, List<LocalSong>> tabSongMap) {
        Command root = createCommand_scan(cacheDir, captureDelay, inputDuration, tabSongMap);
        root.setNext(getCommand_analyze());
        return root;
    }

    public Command getCommand_analyze() {
        Command root = createCommand_analyze();
        root.setNext(getCommand_collectResult());
        return root;
    }

    public Command getCommand_collectResult() {
        return createCommand_collectResult();
    }

    public Command getCommand_uploadRecord(Path accountPath, int uploadDelay) {
        return createCommand_uploadRecord(accountPath, uploadDelay);
    }

    public Command getCommand_loadCachedImages(Path cacheDir,
            Map<String, List<LocalSong>> tabSongMap) {
        return createCommand_loadCachedImages(cacheDir, tabSongMap);
    }

    public TableModel getTaskTableModel() {
        return taskManager.tableModel;
    }

    public TableModel getResultTableModel() {
        return resultManager.tableModel;
    }

    public TableRowSorter<TableModel> getResultTableRowSorter() {
        return resultManager.rowSorter;
    }

    public CollectionTaskData getTaskData(int taskNumber) throws Exception {
        ScannerTask task = taskManager.getTask(taskNumber);
        if (task == null) {
            return null;
        }

        CollectionTaskData data = new CollectionTaskData();

        Exception exception = task.getException();
        if (exception != null) {
            data.exception = exception;
            return data;
        }

        BufferedImage image = task.loadImage();
        data.fullImage = image;

        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(size);
        data.titleImage = area.getTitle(image);

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
                AnalyzedData analyzedData = task.getAnalyzedData(button, pattern);
                CollectionArea.Button b = button.toCollectionArea();
                CollectionArea.Pattern p = pattern.toCollectionArea();

                if (analyzedData != null) {
                    RecordData recordData = new RecordData();
                    recordData.maxCombo = analyzedData.isMaxCombo;
                    recordData.maxComboImage = area.getComboMark(image, b, p);
                    recordData.rate = analyzedData.rateText;
                    recordData.rateImage = area.getRate(image, b, p);

                    data.addRecord(button, pattern, recordData);
                }
            }
        }

        return data;
    }

    protected Command createCommand_scan(Path cachePath, int captureDelay, int inputDuration,
            Map<String, List<LocalSong>> tabSongMap) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_capture.run();

                taskManager.clear();
                taskManager.setCacheDir(cachePath);

                CaptureService captureService = new CaptureService(captureDelay, inputDuration);
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

    protected Command createCommand_collectResult() {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_collectResult.run();

                resultManager.clearRecords();
                resultManager.addRecords(taskManager.getTasks());

                whenDone.run();
                return true;
            }
        };
    }

    protected Command createCommand_uploadRecord(Path accountPath, int uploadDelay) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_uploadRecord.run();

                try {
                    resultManager.upload(accountPath, uploadDelay);
                } catch (Exception e) {
                    whenThrown.accept(e);
                    return false;
                }

                whenDone.run();
                return true;
            }
        };
    }

    protected Command createCommand_loadCachedImages(Path cachePath,
            Map<String, List<LocalSong>> tabSongMap) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_loadImages.run();

                taskManager.clear();
                taskManager.setCacheDir(cachePath);

                tabSongMap.values().forEach((songs) -> {
                    int count = songs.size();
                    for (int i = 0; i < count; ++i) {
                        LocalSong song = songs.get(i);
                        ScannerTask task = taskManager.create(song, i, count);
                        if (Files.exists(task.filePath)) {
                            task.setStatus(Status.CACHED);
                        } else {
                            task.setException(new IOException("File not found"));
                        }
                    }
                });

                whenDone.run();
                return true;
            }
        };
    }
}
