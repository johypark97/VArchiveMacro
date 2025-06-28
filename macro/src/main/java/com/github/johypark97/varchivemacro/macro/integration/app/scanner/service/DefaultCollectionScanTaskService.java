package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleMapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleNormalizer;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.SongTitleMapperFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CollectionScanTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.DefaultCollectionScanTask;
import java.io.IOException;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultCollectionScanTaskService implements CollectionScanTaskService {
    private final CaptureImageRepository captureImageRepository;
    private final CaptureRepository captureRepository;
    private final ConfigRepository configRepository;
    private final SongCaptureLinkRepository songCaptureLinkRepository;
    private final SongRepository songRepository;

    private final OcrFactory songTitleOcrFactory;
    private final SongTitleMapperFactory songTitleMapperFactory;
    private final SongTitleNormalizer songTitleNormalizer;

    public DefaultCollectionScanTaskService(CaptureImageRepository captureImageRepository,
            CaptureRepository captureRepository, ConfigRepository configRepository,
            SongCaptureLinkRepository songCaptureLinkRepository, SongRepository songRepository,
            OcrFactory songTitleOcrFactory, SongTitleMapperFactory songTitleMapperFactory,
            SongTitleNormalizer songTitleNormalizer) {
        this.captureImageRepository = captureImageRepository;
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

        SongTitleMapper songTitleMapper = songTitleMapperFactory.create();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                new DefaultCollectionScanTask(captureImageRepository, captureRepository,
                        songCaptureLinkRepository, songRepository, songTitleOcrFactory,
                        songTitleMapper, songTitleNormalizer, config, selectedCategorySet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
