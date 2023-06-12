package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import com.github.johypark97.varchivemacro.lib.common.protocol.Observers.Observer;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.command.AbstractCommand;
import com.github.johypark97.varchivemacro.macro.core.command.Command;
import com.github.johypark97.varchivemacro.macro.core.ocr.PixError;
import com.github.johypark97.varchivemacro.macro.core.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.DefaultImageCacheManager;
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
    private final Runnable whenDone;
    private final Runnable whenStart_analyze;
    private final Runnable whenStart_capture;
    private final Runnable whenStart_collectResult;
    private final Runnable whenStart_uploadRecord;

    private SongRecordManager songRecordManager;

    public Scanner(Consumer<Exception> whenThrown, Runnable whenCanceled, Runnable whenDone,
            Runnable whenStart_analyze, Runnable whenStart_capture,
            Runnable whenStart_collectResult, Runnable whenStart_uploadRecord) {
        this.whenCanceled = whenCanceled;
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

    public TaskListProvider getTaskListProvider() {
        return taskManager.getTaskListProvider();
    }

    public TaskDataProvider getTaskDataProvider() {
        return this;
    }

    public ResultListProvider getResultListProvider() {
        return resultManager.getResultListProvider();
    }

    public void addTaskListObserver(Observer<ScannerTaskListModels.Event> observer) {
        taskManager.addObserver(observer);
    }

    public void addResultListObserver(Observer<ScannerResultListModels.Event> observer) {
        resultManager.addObserver(observer);
    }

    public Command getCommand_scan(Path cacheDir, int captureDelay, int inputDuration,
            Map<String, List<LocalSong>> tabSongMap) {
        CaptureService captureService = new AlphaCaptureService(captureDelay, inputDuration);
        Command root = createCommand_scan(captureService, cacheDir, tabSongMap);
        root.setNext(getCommand_analyze());
        return root;
    }

    public Command getCommand_safeScan(Path cacheDir, int captureDelay, int inputDuration,
            Map<String, List<LocalSong>> tabSongMap) {
        CaptureService captureService = new SafeCaptureService(captureDelay, inputDuration);
        Command root = createCommand_scan(captureService, cacheDir, tabSongMap);
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

    protected Command createCommand_scan(CaptureService captureService, Path cachePath,
            Map<String, List<LocalSong>> tabSongMap) {
        return new AbstractCommand() {
            @Override
            public boolean run() {
                whenStart_capture.run();

                taskManager.clearTask();
                taskManager.setImageCacheManager(new DefaultImageCacheManager(cachePath));

                captureService.execute(taskManager, tabSongMap);

                // call await twice to allow the user to stop the macro returning to the ALL tab
                boolean canceled = false;
                for (int i = 0; i < 2; ++i) {
                    try {
                        captureService.await();
                    } catch (InterruptedException e) {
                        captureService.shutdownNow();
                        canceled = true;
                    }

                    if (captureService.hasException()) {
                        whenThrown.accept(captureService.getException());
                        return false;
                    }
                }

                if (canceled) {
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

                if (analysisService.exception != null) {
                    whenThrown.accept(analysisService.exception);
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
        TaskData task = taskManager.getTask(taskNumber);
        if (task == null || !task.isValid()) {
            return null;
        }

        ResponseData data = new ResponseData();

        if (task.hasException()) {
            data.exception = task.getException();
            return data;
        }

        BufferedImage image = task.loadImage();
        data.fullImage = image;

        CollectionArea area = task.getCollectionArea();
        byte[] imageBytes = ImageConverter.imageToPngBytes(area.getTitle(image));
        try (PixWrapper pix = new PixWrapper(imageBytes)) {
            PixPreprocessor.preprocessTitle(pix);
            imageBytes = pix.getPngBytes();
        } catch (PixError e) {
            data.exception = e;
            return data;
        }
        data.titleImage = ImageConverter.pngBytesToImage(imageBytes);

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
