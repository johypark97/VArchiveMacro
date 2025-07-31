package com.github.johypark97.varchivemacro.macro.integration.app.scanner.scanner;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.app.CaptureRegionService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FhdDebugCollectionScanTask extends CollectionScanTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(FhdDebugCollectionScanTask.class);

    private final CaptureImageService captureImageService;
    private final CaptureRegionService captureRegionService;

    private int imageIndex;

    public FhdDebugCollectionScanTask(CaptureImageService captureImageService,
            CaptureRegionService captureRegionService, CaptureService captureService,
            PixImageService pixImageService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            Set<String> selectedCategorySet) {
        super(captureService, pixImageService, songService, songTitleService,
                songTitleOcrServiceFactory, selectedCategorySet);

        this.captureImageService = captureImageService;
        this.captureRegionService = captureRegionService;
    }

    @Override
    protected void sleepCaptureDelay() {
    }

    @Override
    protected BufferedImage captureScreen() throws IOException {
        LOGGER.atTrace().log("Loading capture image: {}", imageIndex);

        return captureImageService.findById(imageIndex++);
    }

    @Override
    protected CaptureRegion getCaptureRegion() throws DisplayResolutionException {
        return captureRegionService.create(new Dimension(1920, 1080));
    }

    @Override
    protected void moveToNextCategory() {
    }

    @Override
    protected void moveToNextSong() {
    }

    @Override
    protected void writeCaptureImage(int captureId, BufferedImage image) {
    }

    @Override
    protected Void callTask() throws Exception {
        super.callTask();

        return null;
    }
}
