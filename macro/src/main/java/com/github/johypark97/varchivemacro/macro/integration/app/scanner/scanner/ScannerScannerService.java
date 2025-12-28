package com.github.johypark97.varchivemacro.macro.integration.app.scanner.scanner;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.app.CaptureRegionService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.util.Set;
import javafx.concurrent.Task;

public class ScannerScannerService {
    private final AppConfigService appConfigService;
    private final CaptureImageService captureImageService;
    private final CaptureRegionService captureRegionService;
    private final CaptureService captureService;
    private final PixImageService pixImageService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrServiceFactory songTitleOcrServiceFactory;

    private final Set<String> selectedCategorySet;
    private final boolean debug;

    public ScannerScannerService(AppConfigService appConfigService,
            CaptureImageService captureImageService, CaptureRegionService captureRegionService,
            CaptureService captureService, PixImageService pixImageService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            Set<String> selectedCategorySet, boolean debug) {
        this.appConfigService = appConfigService;
        this.captureImageService = captureImageService;
        this.captureRegionService = captureRegionService;
        this.captureService = captureService;
        this.pixImageService = pixImageService;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrServiceFactory = songTitleOcrServiceFactory;
        this.selectedCategorySet = selectedCategorySet;

        this.debug = debug;
    }

    public Task<Void> createTask() {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = appConfigService.getConfig().scannerConfig();

        return TaskManager.getInstance().register(CollectionScanTask.class,
                debug ? new FhdDebugCollectionScanTask(captureImageService, captureRegionService,
                        captureService, pixImageService, songService, songTitleService,
                        songTitleOcrServiceFactory, selectedCategorySet)
                        : new DefaultCollectionScanTask(captureImageService, captureRegionService,
                                captureService, pixImageService, songService, songTitleService,
                                songTitleOcrServiceFactory, config, selectedCategorySet));
    }

    public boolean stopTask() {
        return TaskManager.Helper.cancel(CollectionScanTask.class);
    }
}
