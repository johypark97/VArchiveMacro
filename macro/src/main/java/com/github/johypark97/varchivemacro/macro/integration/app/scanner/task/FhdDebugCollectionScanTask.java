package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.macro.common.converter.CaptureBoundConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
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

    private int imageIndex;

    public FhdDebugCollectionScanTask(CaptureImageService captureImageService,
            CaptureService captureService, PixImageService pixImageService,
            SongCaptureLinkService songCaptureLinkService, SongService songService,
            SongTitleService songTitleService, OcrServiceFactory songTitleOcrServiceFactory,
            Set<String> selectedCategorySet) {
        super(captureService, pixImageService, songCaptureLinkService, songService,
                songTitleService, songTitleOcrServiceFactory, selectedCategorySet);

        this.captureImageService = captureImageService;
    }

    @Override
    protected BufferedImage captureScreen() {
        try {
            if (imageIndex % 50 == 0) {
                LOGGER.atDebug().log("Loading capture image: {}", imageIndex);
            }

            return captureImageService.findById(imageIndex++);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected CaptureBound getTitleBound() {
        CollectionArea collectionArea;
        try {
            collectionArea = CollectionAreaFactory.create(new Dimension(1920, 1080));
        } catch (NotSupportedResolutionException e) {
            throw new RuntimeException(e); // never be thrown
        }

        return CaptureBoundConverter.fromRectangle(collectionArea.getTitle());
    }

    @Override
    protected void moveToNextCategory() {
    }

    @Override
    protected void moveToNextSong() {
    }

    @Override
    protected void writeImage(int captureId, BufferedImage captureImage) {
    }

    @Override
    protected Void callTask() throws Exception {
        super.callTask();

        return null;
    }
}
