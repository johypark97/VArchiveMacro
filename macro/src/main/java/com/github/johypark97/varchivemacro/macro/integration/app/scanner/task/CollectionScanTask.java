package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import java.awt.image.BufferedImage;
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

    protected abstract BufferedImage captureScreen() throws InterruptedException;

    protected abstract CaptureBound getTitleBound();

    protected abstract void moveToNextCategory() throws InterruptedException;

    protected abstract void moveToNextSong() throws InterruptedException;

    protected abstract void writeImage(int captureId, BufferedImage captureImage)
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

    private String readTitle(OcrService ocrService, BufferedImage image, CaptureBound titleBound)
            throws IOException, PixImageException {
        BufferedImage titleImage =
                image.getSubimage(titleBound.x(), titleBound.y(), titleBound.width(),
                        titleBound.height());

        try (PixImage pix = pixImageService.createPixImage(titleImage)) {
            pixImageService.preprocessTitle(pix);
            return ocrService.run(pix);
        }
    }

    @Override
    protected Void callTask() throws Exception {
        if (!captureService.isEmpty()) {
            throw new IllegalStateException();
        }

        // create a queue that filtered by selectedCategorySet
        Queue<Song.Pack.Category> categoryQueue = createCategoryQueue();

        CaptureBound titleBound = getTitleBound();

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

                    // capture the screen
                    BufferedImage captureImage = captureScreen();

                    // read title
                    String scannedTitle = readTitle(ocrService, captureImage, titleBound);
                    scannedTitle = songTitleService.normalizeTitle(scannedTitle);

                    // store cache data and image
                    Capture capture = new Capture(category, scannedTitle, titleBound);
                    CaptureEntry captureEntry = captureService.save(capture);

                    writeImage(captureEntry.entryId(), captureImage);

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
