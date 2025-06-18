package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.SongTitleMapperFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.task.CollectionScanTask;
import com.github.johypark97.varchivemacro.macro.application.scanner.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.cache.CaptureImageCache;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleMapper;
import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleNormalizer;
import java.io.IOException;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultCollectionScanTaskService implements CollectionScanTaskService {
    private final CaptureRepository captureRepository;
    private final ConfigRepository configRepository;
    private final SongCaptureLinkRepository songCaptureLinkRepository;
    private final SongRepository songRepository;

    private final CaptureImageCacheFactory captureImageCacheFactory;
    private final OcrFactory songTitleOcrFactory;
    private final SongTitleMapperFactory songTitleMapperFactory;
    private final SongTitleNormalizer songTitleNormalizer;

    public DefaultCollectionScanTaskService(CaptureRepository captureRepository,
            ConfigRepository configRepository, SongCaptureLinkRepository songCaptureLinkRepository,
            SongRepository songRepository, CaptureImageCacheFactory captureImageCacheFactory,
            OcrFactory songTitleOcrFactory, SongTitleMapperFactory songTitleMapperFactory,
            SongTitleNormalizer songTitleNormalizer) {
        this.captureImageCacheFactory = captureImageCacheFactory;
        this.captureRepository = captureRepository;
        this.configRepository = configRepository;
        this.songCaptureLinkRepository = songCaptureLinkRepository;
        this.songRepository = songRepository;
        this.songTitleMapperFactory = songTitleMapperFactory;
        this.songTitleNormalizer = songTitleNormalizer;
        this.songTitleOcrFactory = songTitleOcrFactory;
    }

    @Override
    public Task<Void> createTask(Set<String> selectedCategorySet) throws IOException {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configRepository.findScannerConfig();

        CaptureImageCache captureImageCache =
                captureImageCacheFactory.create(config.cacheDirectory());

        SongTitleMapper songTitleMapper = songTitleMapperFactory.create();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                new DefaultCollectionScanTask(captureRepository, songCaptureLinkRepository,
                        songRepository, captureImageCache, songTitleOcrFactory, songTitleMapper,
                        songTitleNormalizer, config, selectedCategorySet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
