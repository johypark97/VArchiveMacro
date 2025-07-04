package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CollectionScanTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.DefaultCollectionScanTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.FhdDebugCollectionScanTask;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultCollectionScanTaskService implements CollectionScanTaskService {
    private final CaptureImageService captureImageService;
    private final CaptureService captureService;
    private final ConfigService configService;
    private final PixImageService pixImageService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrServiceFactory songTitleOcrServiceFactory;

    private final boolean debug;

    public DefaultCollectionScanTaskService(CaptureImageService captureImageService,
            CaptureService captureService, ConfigService configService,
            PixImageService pixImageService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            boolean debug) {
        this.captureImageService = captureImageService;
        this.captureService = captureService;
        this.configService = configService;
        this.pixImageService = pixImageService;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrServiceFactory = songTitleOcrServiceFactory;

        this.debug = debug;
    }

    @Override
    public Task<Void> createTask(Set<String> selectedCategorySet) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configService.findScannerConfig();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                debug
                        ? new FhdDebugCollectionScanTask(captureImageService, captureService,
                        pixImageService, songService, songTitleService, songTitleOcrServiceFactory,
                        selectedCategorySet)
                        : new DefaultCollectionScanTask(captureImageService, captureService,
                                pixImageService, songService, songTitleService,
                                songTitleOcrServiceFactory, config, selectedCategorySet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
