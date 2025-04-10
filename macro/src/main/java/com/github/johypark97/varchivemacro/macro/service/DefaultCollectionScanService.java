package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.domain.CacheManager;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.service.task.AbstractCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.service.task.DefaultCollectionScanTask;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javafx.concurrent.Task;

public class DefaultCollectionScanService implements CollectionScanService {
    private final ConfigRepository configRepository;
    private final DatabaseRepository databaseRepository;

    private final ScanDataDomain scanDataDomain;

    public DefaultCollectionScanService(ConfigRepository configRepository,
            DatabaseRepository databaseRepository, ScanDataDomain scanDataDomain) {
        this.configRepository = configRepository;
        this.databaseRepository = databaseRepository;
        this.scanDataDomain = scanDataDomain;
    }

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheManager(path).validate();
    }

    @Override
    public boolean isReady_collectionScan() {
        return scanDataDomain.isEmpty();
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
    public void clearScanData(Runnable onClear) {
        if (TaskManager.getInstance().isRunningAny()) {
            return;
        }

        scanDataDomain.clear();

        onClear.run();
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
        return scanDataDomain.getCaptureImage(id,
                configRepository.getScannerConfig().cacheDirectory);
    }
}
