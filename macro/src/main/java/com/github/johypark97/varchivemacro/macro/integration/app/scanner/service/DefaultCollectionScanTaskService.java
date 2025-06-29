package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CollectionScanTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.DefaultCollectionScanTask;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultCollectionScanTaskService implements CollectionScanTaskService {
    private final CaptureImageService captureImageService;
    private final CaptureRepository captureRepository;
    private final ConfigService configService;
    private final SongCaptureLinkRepository songCaptureLinkRepository;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrFactory songTitleOcrFactory;

    public DefaultCollectionScanTaskService(CaptureImageService captureImageService,
            CaptureRepository captureRepository, ConfigService configService,
            SongCaptureLinkRepository songCaptureLinkRepository, SongService songService,
            SongTitleService songTitleService, OcrFactory songTitleOcrFactory) {
        this.captureImageService = captureImageService;
        this.captureRepository = captureRepository;
        this.configService = configService;
        this.songCaptureLinkRepository = songCaptureLinkRepository;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrFactory = songTitleOcrFactory;
    }

    @Override
    public Task<Void> createTask(Set<String> selectedCategorySet) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configService.findScannerConfig();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                new DefaultCollectionScanTask(captureImageService, captureRepository,
                        songCaptureLinkRepository, songService, songTitleService,
                        songTitleOcrFactory, config, selectedCategorySet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
