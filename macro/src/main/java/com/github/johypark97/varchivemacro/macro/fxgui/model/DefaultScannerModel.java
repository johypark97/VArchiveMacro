package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.RecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.service.ScannerService;
import com.github.johypark97.varchivemacro.macro.service.task.AnalysisTask;
import com.github.johypark97.varchivemacro.macro.service.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.service.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.service.task.UploadTask;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class DefaultScannerModel implements ScannerModel {
    private final AnalysisDataManager analysisDataManager = new AnalysisDataManager();
    private final NewRecordDataManager newRecordDataManager = new NewRecordDataManager();
    private final ScanDataManager scanDataManager = new ScanDataManager();

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheManager(path).validate();
    }

    @Override
    public void setupService(Consumer<Throwable> onThrow) {
        EventHandler<WorkerStateEvent> onFailedEventHandler =
                event -> onThrow.accept(event.getSource().getException());

        CollectionScanService collectionScanService =
                ServiceManager.getInstance().create(CollectionScanService.class);
        if (collectionScanService == null) {
            throw new IllegalStateException("CollectionScanService has already been created.");
        }
        collectionScanService.setOnFailed(onFailedEventHandler);

        ScannerService scannerService = ServiceManager.getInstance().create(ScannerService.class);
        if (scannerService == null) {
            throw new IllegalStateException("ScannerService has already been created.");
        }
        scannerService.setOnFailed(onFailedEventHandler);
    }

    @Override
    public void startCollectionScan(Runnable onDone, Runnable onCancel,
            Map<String, List<Song>> categoryNameSongListMap, TitleTool titleTool,
            Set<String> selectedCategorySet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        CollectionScanService service = Objects.requireNonNull(
                ServiceManager.getInstance().get(CollectionScanService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new DefaultCollectionScanTask(scanDataManager, categoryNameSongListMap,
                            titleTool, selectedCategorySet, cacheDirectoryPath, captureDelay,
                            keyInputDuration);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        // service.setTaskConstructor(() -> {
        //     Task<Void> task =
        //             new FHDCollectionLoaderTask(scanDataManager, categoryNameSongListMap, titleTool,
        //                     selectedCategorySet, cacheDirectoryPath);
        //
        //     task.setOnCancelled(event -> onCancel.run());
        //     task.setOnSucceeded(event -> onDone.run());
        //
        //     return task;
        // });

        service.reset();
        service.start();
    }

    @Override
    public void stopCollectionScan() {
        ServiceManagerHelper.stopService(CollectionScanService.class);
    }

    @Override
    public void starAnalysis(Consumer<Double> onUpdateProgress, Runnable onDataReady,
            Runnable onDone, Runnable onCancel, Path cacheDirectoryPath, int analysisThreadCount) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new AnalysisTask(onDataReady, scanDataManager, analysisDataManager,
                    cacheDirectoryPath, analysisThreadCount);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            task.progressProperty().addListener(
                    (observable, oldValue, newValue) -> onUpdateProgress.accept(
                            newValue.doubleValue()));

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void stopAnalysis() {
        ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public void collectNewRecord(Runnable onDone, RecordRepository recordRepository) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new CollectNewRecordTask(recordRepository, analysisDataManager,
                    newRecordDataManager);

            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void startUpload(Runnable onDone, Runnable onCancel,
            DatabaseRepository databaseRepository, RecordRepository recordRepository,
            Path accountPath, int recordUploadDelay) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new UploadTask(databaseRepository, recordRepository, newRecordDataManager,
                            accountPath, recordUploadDelay);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void stopUpload() {
        ServiceManagerHelper.stopService(ScannerService.class);
    }

    @Override
    public boolean isScanDataEmpty() {
        return scanDataManager.isEmpty();
    }

    @Override
    public void clearScanData(Runnable onClear) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return;
        }

        scanDataManager.clear();

        onClear.run();
    }

    @Override
    public boolean isAnalysisDataEmpty() {
        return analysisDataManager.isEmpty();
    }

    @Override
    public void clearAnalysisData(Runnable onClear) {
        ScannerService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerService.class));
        if (service.isRunning()) {
            return;
        }

        analysisDataManager.clear();
        newRecordDataManager.clear();

        onClear.run();
    }

    @Override
    public boolean isNewRecordDataEmpty() {
        return newRecordDataManager.isEmpty();
    }

    @Override
    public CaptureData getCaptureData(int id) {
        return scanDataManager.getCaptureData(id);
    }

    @Override
    public List<CaptureData> copyCaptureDataList() {
        return scanDataManager.copyCaptureDataList();
    }

    @Override
    public SongData getSongData(int id) {
        return scanDataManager.getSongData(id);
    }

    @Override
    public List<SongData> copySongDataList() {
        return scanDataManager.copySongDataList();
    }

    @Override
    public BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException {
        CaptureData captureData = scanDataManager.getCaptureData(id);

        try {
            return new CacheManager(cacheDirectoryPath).read(id);
        } catch (IOException e) {
            captureData.exception.set(e);
            throw e;
        }
    }

    @Override
    public List<AnalysisData> copyAnalysisDataList() {
        return analysisDataManager.copyAnalysisDataList();
    }

    @Override
    public AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id)
            throws Exception {
        AnalyzedRecordData data = new AnalyzedRecordData();

        AnalysisData analysisData = analysisDataManager.getAnalysisData(id);
        data.song = analysisData.songDataProperty().get().songProperty().get();

        BufferedImage image = getCaptureImage(cacheDirectoryPath,
                analysisData.captureData.get().idProperty().get());
        Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
        CollectionArea area = CollectionAreaFactory.create(resolution);

        data.titleImage = area.getTitle(image);
        data.titleText = analysisData.captureData.get().scannedTitle.get();

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
                data.rateImage[button.getWeight()][pattern.getWeight()] =
                        area.getRate(image, button, pattern);

                data.maxComboImage[button.getWeight()][pattern.getWeight()] =
                        area.getComboMark(image, button, pattern);

                RecordData recordData = analysisData.recordDataTable.get(button, pattern);
                if (recordData == null) {
                    continue;
                }

                data.rateText[button.getWeight()][pattern.getWeight()] = recordData.rateText.get();

                data.maxCombo[button.getWeight()][pattern.getWeight()] = recordData.maxCombo.get();
            }
        }

        return data;
    }

    @Override
    public List<NewRecordData> copyNewRecordDataList() {
        return newRecordDataManager.copyNewRecordDataList();
    }
}
