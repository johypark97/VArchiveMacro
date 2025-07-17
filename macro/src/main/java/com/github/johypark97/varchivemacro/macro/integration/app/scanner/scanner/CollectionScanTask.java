package com.github.johypark97.varchivemacro.macro.integration.app.scanner.scanner;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.exception.DisplayResolutionException;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public abstract class CollectionScanTask extends InterruptibleTask<Void> {
    private static final String CATEGORY_NAME_CLEAR_PASS_PLUS = "CLEARPASS+";
    private static final int CAPTURE_DUPLICATE_LIMIT = 2;

    private final CaptureService captureService;
    private final PixImageService pixImageService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final OcrServiceFactory songTitleOcrServiceFactory;

    private final Set<String> selectedCategorySet;

    public CollectionScanTask(CaptureService captureService, PixImageService pixImageService,
            SongService songService, SongTitleService songTitleService,
            OcrServiceFactory songTitleOcrServiceFactory, Set<String> selectedCategorySet) {
        this.captureService = captureService;
        this.pixImageService = pixImageService;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.songTitleOcrServiceFactory = songTitleOcrServiceFactory;

        this.selectedCategorySet = selectedCategorySet;
    }

    protected abstract void sleepCaptureDelay() throws InterruptedException;

    protected abstract PngImage captureScreen() throws IOException;

    protected abstract CaptureRegion getCaptureRegion() throws DisplayResolutionException;

    protected abstract void moveToNextCategory() throws InterruptedException;

    protected abstract void moveToNextSong() throws InterruptedException;

    protected abstract void writeCaptureImage(int captureId, PngImage pngImage)
            throws InterruptedException;

    private Queue<Song.Pack.Category> createCategoryQueue() {
        Queue<Song.Pack.Category> queue = new LinkedList<>();

        songService.findAllCategory().forEach(category -> {
            // add a null value before the ClearPass+ category to skip the favorite category
            if (CATEGORY_NAME_CLEAR_PASS_PLUS.equals(category.name())) {
                queue.add(null);
            }

            queue.add(selectedCategorySet.contains(category.name()) ? category : null);
        });

        return queue;
    }

    private String readTitle(OcrService ocrService, PngImage image, Rectangle titleRectangle)
            throws PixImageException {
        try (PixImage pix = pixImageService.createPixImage(image.data())) {
            try (PixImage titlePix = pix.crop(titleRectangle)) {
                pixImageService.preprocessTitle(titlePix);
                return ocrService.run(titlePix);
            }
        }
    }

    @Override
    protected Void callTask() throws Exception {
        if (!captureService.isEmpty()) {
            throw new IllegalStateException();
        }

        // check if the screen resolution is supported using whether an exception occurs
        CaptureRegion region = getCaptureRegion();

        // create a queue that filtered by selectedCategorySet
        Queue<Song.Pack.Category> categoryQueue = createCategoryQueue();

        // run main task
        try (OcrService ocrService = songTitleOcrServiceFactory.create()) {
            while (!categoryQueue.isEmpty()) {
                Song.Pack.Category category = categoryQueue.poll();

                moveToNextCategory();

                if (category == null) {
                    continue;
                }

                String previousTitle = "";
                boolean firstCapture = true;
                int duplicateCount = 0;
                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }

                    if (firstCapture) {
                        firstCapture = false;
                    } else {
                        moveToNextSong();
                    }

                    // wait as long as captureDelay before capture
                    sleepCaptureDelay();

                    // capture the screen
                    PngImage captureImage = captureScreen();

                    // read title
                    String scannedTitle = readTitle(ocrService, captureImage, region.getTitle());
                    scannedTitle = songTitleService.normalizeTitle(scannedTitle);
                    scannedTitle = songTitleService.remapScannedTitle(scannedTitle);

                    // store cache data and image
                    Capture capture = new Capture(category, scannedTitle, region);
                    CaptureEntry captureEntry = captureService.save(capture);

                    writeCaptureImage(captureEntry.entryId(), captureImage);

                    // check duplication and break the loop
                    if (!scannedTitle.equals(previousTitle)) {
                        duplicateCount = 0;
                    } else {
                        duplicateCount++;

                        if (duplicateCount >= CAPTURE_DUPLICATE_LIMIT) {
                            break;
                        }
                    }

                    // store scannedTitle as the previousTitle
                    previousTitle = scannedTitle;
                }
            }
        } catch (InterruptedException ignored) {
        }

        // return to the ALL tab
        try {
            int count = categoryQueue.size() + 1;
            for (int i = 0; i < count; i++) {
                moveToNextCategory();
            }
        } catch (InterruptedException ignored) {
        }

        return null;
    }
}
