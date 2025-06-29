package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CollectionScanTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.DefaultCollectionScanTask;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultCollectionScanTaskService implements CollectionScanTaskService {
    private final CaptureImageRepository captureImageRepository;
    private final CaptureRepository captureRepository;
    private final ConfigRepository configRepository;
    private final SongCaptureLinkRepository songCaptureLinkRepository;
    private final SongRepository songRepository;

    private final SongTitleService songTitleService;

    private final OcrFactory songTitleOcrFactory;

    public DefaultCollectionScanTaskService(CaptureImageRepository captureImageRepository,
            CaptureRepository captureRepository, ConfigRepository configRepository,
            SongCaptureLinkRepository songCaptureLinkRepository, SongRepository songRepository,
            SongTitleService songTitleService, OcrFactory songTitleOcrFactory) {
        this.captureImageRepository = captureImageRepository;
        this.captureRepository = captureRepository;
        this.configRepository = configRepository;
        this.songCaptureLinkRepository = songCaptureLinkRepository;
        this.songRepository = songRepository;

        this.songTitleService = songTitleService;

        this.songTitleOcrFactory = songTitleOcrFactory;
    }

    @Override
    public Task<Void> createTask(Set<String> selectedCategorySet) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configRepository.findScannerConfig();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                new DefaultCollectionScanTask(captureImageRepository, captureRepository,
                        songCaptureLinkRepository, songRepository, songTitleService,
                        songTitleOcrFactory, config, selectedCategorySet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
