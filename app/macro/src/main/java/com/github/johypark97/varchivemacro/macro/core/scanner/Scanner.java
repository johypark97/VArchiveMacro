package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.DefaultResultManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.DefaultTaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResultListProvider;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.TaskListProvider;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.TaskDataProvider;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Scanner implements TaskDataProvider {
    private final DefaultTaskManager taskManager = new DefaultTaskManager();
    private final DefaultResultManager resultManager = new DefaultResultManager();

    private final Consumer<Exception> whenThrown;
    private final Runnable whenCanceled;
    private final Runnable whenCaptureDone;
    private final Runnable whenDone;
    private final Runnable whenStart_analyze;
    private final Runnable whenStart_capture;
    private final Runnable whenStart_collectResult;
    private final Runnable whenStart_uploadRecord;

    private SongRecordManager songRecordManager;

    public Scanner(Consumer<Exception> whenThrown, Runnable whenCanceled, Runnable whenCaptureDone,
            Runnable whenDone, Runnable whenStart_analyze, Runnable whenStart_capture,
            Runnable whenStart_collectResult, Runnable whenStart_uploadRecord) {
        this.whenCanceled = whenCanceled;
        this.whenCaptureDone = whenCaptureDone;
        this.whenDone = whenDone;
        this.whenStart_analyze = whenStart_analyze;
        this.whenStart_capture = whenStart_capture;
        this.whenStart_collectResult = whenStart_collectResult;
        this.whenStart_uploadRecord = whenStart_uploadRecord;
        this.whenThrown = whenThrown;
    }

    public void setModels(SongRecordManager songRecordManager) {
        this.songRecordManager = songRecordManager;

        resultManager.setModels(songRecordManager);
    }

    public void addTaskListClient(Client<ScannerTaskListModels.Event, TaskListProvider> client) {
        taskManager.addClient(client);
    }

    public void addResultListClient(
            Client<ScannerResultListModels.Event, ResultListProvider> client) {
        resultManager.addClient(client);
    }

    public TaskDataProvider getTaskDataProvider() {
        return this;
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

                resultManager.clear();
                resultManager.addAll(taskManager);

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

                UploadService uploadService =
                        new UploadService(songRecordManager, accountPath, uploadDelay);
                uploadService.execute(resultManager);

                try {
                    uploadService.await();
                } catch (InterruptedException e) {
                    uploadService.shutdownNow();
                    whenCanceled.run();
                    return false;
                }

                if (uploadService.exception != null) {
                    whenThrown.accept(uploadService.exception);
                    return false;
                }

                whenDone.run();
                return true;
            }
        };
    }

    @Override
    public ResponseData getValue(int taskNumber) throws IOException {
        TaskData task = taskManager.getTaskData(taskNumber);
        if (task == null) {
            return null;
        }

        ResponseData data = new ResponseData();

        Exception exception = task.getException();
        if (exception != null) {
            data.exception = exception;
            return data;
        }

        BufferedImage image = task.loadImage();
        data.fullImage = image;

        CollectionArea area = task.getCollectionArea();
        data.titleImage = area.getTitle(image);

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
                AnalyzedData analyzedData = task.getAnalyzedData(button, pattern);
                CollectionArea.Button b = button.toCollectionArea();
                CollectionArea.Pattern p = pattern.toCollectionArea();

                if (analyzedData != null) {
                    RecordData recordData = new RecordData();
                    recordData.maxCombo = analyzedData.isMaxCombo();
                    recordData.maxComboImage = area.getComboMark(image, b, p);
                    recordData.rate = analyzedData.getRateText();
                    recordData.rateImage = area.getRate(image, b, p);

                    data.addRecord(button, pattern, recordData);
                }
            }
        }

        return data;
    }
}
