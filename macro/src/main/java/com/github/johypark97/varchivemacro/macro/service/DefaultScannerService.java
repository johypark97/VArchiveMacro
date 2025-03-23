package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.CacheManager;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;
import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import com.github.johypark97.varchivemacro.macro.model.RecordData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.service.fxservice.CollectionScanFxService;
import com.github.johypark97.varchivemacro.macro.service.fxservice.ScannerFxService;
import com.github.johypark97.varchivemacro.macro.service.task.AnalysisTask;
import com.github.johypark97.varchivemacro.macro.service.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.service.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.service.task.UploadTask;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class DefaultScannerService implements ScannerService {
    private final DatabaseRepository databaseRepository;
    private final RecordRepository recordRepository;

    private final AnalysisDataDomain analysisDataDomain;
    private final NewRecordDataDomain newRecordDataDomain;
    private final ScanDataDomain scanDataDomain;

    public DefaultScannerService(DatabaseRepository databaseRepository,
            RecordRepository recordRepository, AnalysisDataDomain analysisDataDomain,
            NewRecordDataDomain newRecordDataDomain, ScanDataDomain scanDataDomain) {
        this.analysisDataDomain = analysisDataDomain;
        this.databaseRepository = databaseRepository;
        this.newRecordDataDomain = newRecordDataDomain;
        this.recordRepository = recordRepository;
        this.scanDataDomain = scanDataDomain;
    }

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheManager(path).validate();
    }

    @Override
    public void setupService(Consumer<Throwable> onThrow) {
        EventHandler<WorkerStateEvent> onFailedEventHandler =
                event -> onThrow.accept(event.getSource().getException());

        CollectionScanFxService collectionScanFxService =
                ServiceManager.getInstance().create(CollectionScanFxService.class);
        if (collectionScanFxService == null) {
            throw new IllegalStateException("CollectionScanFxService has already been created.");
        }
        collectionScanFxService.setOnFailed(onFailedEventHandler);

        ScannerFxService scannerFxService =
                ServiceManager.getInstance().create(ScannerFxService.class);
        if (scannerFxService == null) {
            throw new IllegalStateException("ScannerFxService has already been created.");
        }
        scannerFxService.setOnFailed(onFailedEventHandler);
    }

    @Override
    public void startCollectionScan(Runnable onDone, Runnable onCancel,
            Set<String> selectedCategorySet, Path cacheDirectoryPath, int captureDelay,
            int keyInputDuration) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        CollectionScanFxService service = Objects.requireNonNull(
                ServiceManager.getInstance().get(CollectionScanFxService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new DefaultCollectionScanTask(scanDataDomain,
                    databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
                    selectedCategorySet, cacheDirectoryPath, captureDelay, keyInputDuration);

            // Task<Void> task = new FHDCollectionLoaderTask(scanDataDomain,
            //         databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
            //         selectedCategorySet, cacheDirectoryPath);

            task.setOnCancelled(event -> onCancel.run());
            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void stopCollectionScan() {
        ServiceManagerHelper.stopService(CollectionScanFxService.class);
    }

    @Override
    public void startAnalysis(Consumer<Double> onUpdateProgress, Runnable onDataReady,
            Runnable onDone, Runnable onCancel, Path cacheDirectoryPath, int analysisThreadCount) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerFxService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new AnalysisTask(onDataReady, scanDataDomain, analysisDataDomain,
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
        ServiceManagerHelper.stopService(ScannerFxService.class);
    }

    @Override
    public void collectNewRecord(Runnable onDone) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerFxService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task = new CollectNewRecordTask(recordRepository, analysisDataDomain,
                    newRecordDataDomain);

            task.setOnSucceeded(event -> onDone.run());

            return task;
        });

        service.reset();
        service.start();
    }

    @Override
    public void startUpload(Runnable onDone, Runnable onCancel, Path accountPath,
            int recordUploadDelay) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        ScannerFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerFxService.class));

        service.setTaskConstructor(() -> {
            Task<Void> task =
                    new UploadTask(databaseRepository, recordRepository, newRecordDataDomain,
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
        ServiceManagerHelper.stopService(ScannerFxService.class);
    }

    @Override
    public boolean isScanDataEmpty() {
        return scanDataDomain.isEmpty();
    }

    @Override
    public void clearScanData(Runnable onClear) {
        ScannerFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerFxService.class));
        if (service.isRunning()) {
            return;
        }

        scanDataDomain.clear();

        onClear.run();
    }

    @Override
    public boolean isAnalysisDataEmpty() {
        return analysisDataDomain.isEmpty();
    }

    @Override
    public void clearAnalysisData(Runnable onClear) {
        ScannerFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(ScannerFxService.class));
        if (service.isRunning()) {
            return;
        }

        analysisDataDomain.clear();
        newRecordDataDomain.clear();

        onClear.run();
    }

    @Override
    public boolean isNewRecordDataEmpty() {
        return newRecordDataDomain.isEmpty();
    }

    @Override
    public CaptureData getCaptureData(int id) {
        return scanDataDomain.getCaptureData(id);
    }

    @Override
    public List<CaptureData> copyCaptureDataList() {
        return scanDataDomain.copyCaptureDataList();
    }

    @Override
    public SongData getSongData(int id) {
        return scanDataDomain.getSongData(id);
    }

    @Override
    public List<SongData> copySongDataList() {
        return scanDataDomain.copySongDataList();
    }

    @Override
    public BufferedImage getCaptureImage(Path cacheDirectoryPath, int id) throws IOException {
        CaptureData captureData = scanDataDomain.getCaptureData(id);

        try {
            return new CacheManager(cacheDirectoryPath).read(id);
        } catch (IOException e) {
            captureData.exception.set(e);
            throw e;
        }
    }

    @Override
    public List<AnalysisData> copyAnalysisDataList() {
        return analysisDataDomain.copyAnalysisDataList();
    }

    @Override
    public AnalyzedRecordData getAnalyzedRecordData(Path cacheDirectoryPath, int id)
            throws Exception {
        AnalyzedRecordData data = new AnalyzedRecordData();

        AnalysisData analysisData = analysisDataDomain.getAnalysisData(id);
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
        return newRecordDataDomain.copyNewRecordDataList();
    }
}
