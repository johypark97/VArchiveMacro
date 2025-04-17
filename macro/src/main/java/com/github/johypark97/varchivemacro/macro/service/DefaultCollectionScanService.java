package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import com.github.johypark97.varchivemacro.macro.repository.CacheRepository;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.ScanDataRepository;
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
    private final ScanDataRepository scanDataRepository;

    public DefaultCollectionScanService(ConfigRepository configRepository,
            DatabaseRepository databaseRepository, ScanDataRepository scanDataRepository) {
        this.configRepository = configRepository;
        this.databaseRepository = databaseRepository;
        this.scanDataRepository = scanDataRepository;
    }

    @Override
    public void validateCacheDirectory(Path path) throws IOException {
        new CacheRepository(path).validate();
    }

    @Override
    public boolean isReady_collectionScan() {
        return scanDataRepository.isEmpty();
    }

    @Override
    public Task<Void> createTask_collectionScan() {
        ScannerConfig config = configRepository.getScannerConfig();

        AbstractCollectionScanTask task = new DefaultCollectionScanTask(scanDataRepository,
                databaseRepository.categoryNameSongListMap(), databaseRepository.getTitleTool(),
                config.selectedCategorySet, config.cacheDirectory, config.captureDelay,
                config.keyInputDuration);

        // AbstractCollectionScanTask task = new FHDCollectionLoaderTask(scanDataRepository,
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

        scanDataRepository.clear();

        onClear.run();
    }

    @Override
    public CaptureData getCaptureData(int id) {
        return scanDataRepository.getCaptureData(id);
    }

    @Override
    public List<CaptureData> copyCaptureDataList() {
        return scanDataRepository.copyCaptureDataList();
    }

    @Override
    public SongData getSongData(int id) {
        return scanDataRepository.getSongData(id);
    }

    @Override
    public List<SongData> copySongDataList() {
        return scanDataRepository.copySongDataList();
    }

    @Override
    public BufferedImage getCaptureImage(int id) throws IOException {
        return scanDataRepository.getCaptureImage(id,
                configRepository.getScannerConfig().cacheDirectory);
    }
}
