package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
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
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.service.task.AbstractCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.service.task.AnalysisTask;
import com.github.johypark97.varchivemacro.macro.service.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.service.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.service.task.UploadTask;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.concurrent.Task;

public class DefaultScannerService implements ScannerService {
    private final ConfigRepository configRepository;
    private final DatabaseRepository databaseRepository;
    private final RecordRepository recordRepository;

    private final AnalysisDataDomain analysisDataDomain;
    private final NewRecordDataDomain newRecordDataDomain;
    private final ScanDataDomain scanDataDomain;

    public DefaultScannerService(ConfigRepository configRepository,
            DatabaseRepository databaseRepository, RecordRepository recordRepository,
            AnalysisDataDomain analysisDataDomain, NewRecordDataDomain newRecordDataDomain,
            ScanDataDomain scanDataDomain) {
        this.analysisDataDomain = analysisDataDomain;
        this.configRepository = configRepository;
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
    public Task<Void> createTask_collectionScan() {
        ScannerConfig config = configRepository.getScannerConfig();

        AbstractCollectionScanTask task = new DefaultCollectionScanTask(scanDataDomain,
                databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
                config.selectedCategorySet, config.cacheDirectory, config.captureDelay,
                config.keyInputDuration);

        // AbstractCollectionScanTask task = new FHDCollectionLoaderTask(scanDataDomain,
        //         databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
        //         config.selectedCategorySet, config.cacheDirectory);

        return TaskManager.getInstance().register(AbstractCollectionScanTask.class, task);
    }

    @Override
    public void stopTask_collectionScan() {
        TaskManager.Helper.cancel(AbstractCollectionScanTask.class);
    }

    @Override
    public Task<Void> createTask_analysis(Runnable onDataReady) {
        ScannerConfig config = configRepository.getScannerConfig();

        return TaskManager.getInstance().register(AnalysisTask.class,
                new AnalysisTask(onDataReady, scanDataDomain, analysisDataDomain,
                        config.cacheDirectory, config.analysisThreadCount));
    }

    @Override
    public void stopTask_analysis() {
        TaskManager.Helper.cancel(AnalysisTask.class);
    }

    @Override
    public Task<Void> createTask_collectNewRecord() {
        return TaskManager.getInstance().register(CollectNewRecordTask.class,
                new CollectNewRecordTask(recordRepository, analysisDataDomain,
                        newRecordDataDomain));
    }

    @Override
    public Task<Void> createTask_startUpload() {
        ScannerConfig config = configRepository.getScannerConfig();

        return TaskManager.getInstance().register(UploadTask.class,
                new UploadTask(databaseRepository, recordRepository, newRecordDataDomain,
                        config.accountFile, config.recordUploadDelay));
    }

    @Override
    public void stopTask_upload() {
        TaskManager.Helper.cancel(UploadTask.class);
    }

    @Override
    public boolean isScanDataEmpty() {
        return scanDataDomain.isEmpty();
    }

    @Override
    public void clearScanData(Runnable onClear) {
        if (TaskManager.getInstance().isRunningAny()) {
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
        if (TaskManager.getInstance().isRunningAny()) {
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
    public BufferedImage getCaptureImage(int id) throws IOException {
        CacheManager cacheManager =
                new CacheManager(configRepository.getScannerConfig().cacheDirectory);

        try {
            return cacheManager.read(id);
        } catch (IOException e) {
            scanDataDomain.getCaptureData(id).exception.set(e);
            throw e;
        }
    }

    @Override
    public List<AnalysisData> copyAnalysisDataList() {
        return analysisDataDomain.copyAnalysisDataList();
    }

    @Override
    public AnalyzedRecordData getAnalyzedRecordData(int id) throws Exception {
        AnalyzedRecordData data = new AnalyzedRecordData();

        AnalysisData analysisData = analysisDataDomain.getAnalysisData(id);
        data.song = analysisData.songDataProperty().get().songProperty().get();

        BufferedImage image = getCaptureImage(analysisData.captureData.get().idProperty().get());
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
