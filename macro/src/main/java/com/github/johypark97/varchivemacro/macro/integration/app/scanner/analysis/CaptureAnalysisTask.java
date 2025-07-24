package com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis;

import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.exception.PixImageException;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.domain.model.PixImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import com.google.common.base.CharMatcher;
import java.awt.Rectangle;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaptureAnalysisTask
        extends InterruptibleTask<Map<Integer, CaptureAnalysisTaskResult>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureAnalysisTask.class);

    private static final float COMBO_MARK_RATIO = 0.01f;
    private static final float RATE_RATIO = 0.01f;
    private static final int COMBO_MARK_FACTOR = 8;
    private static final int COMBO_MARK_THRESHOLD = 224;
    private static final int RATE_FACTOR = 8;
    private static final int RATE_THRESHOLD = 224;

    private final CaptureImageService captureImageService;
    private final PixImageService pixImageService;

    private final OcrServiceFactory commonOcrServiceFactory;

    private final List<CaptureEntry> captureEntryList;

    private final int analyzerThreadCount;
    private final int imagePreloaderQueueCapacity;
    private final int imagePreloaderThreadCount;

    private int analyzedCaptureImageCount;
    private int queuedCaptureImageCount;

    public CaptureAnalysisTask(CaptureImageService captureImageService,
            PixImageService pixImageService, OcrServiceFactory commonOcrServiceFactory,
            ScannerConfig config, List<CaptureEntry> captureEntryList) {
        this.captureImageService = captureImageService;
        this.pixImageService = pixImageService;

        this.commonOcrServiceFactory = commonOcrServiceFactory;

        this.captureEntryList = captureEntryList;

        analyzerThreadCount = Math.max(1, config.analyzerThreadCount());
        imagePreloaderQueueCapacity = analyzerThreadCount * 3 / 2;
        imagePreloaderThreadCount = (int) (Math.log(analyzerThreadCount + 1) / Math.log(2));
    }

    private void analyze(OcrService ocrService, CaptureEntry captureEntry, PngImage pngImage)
            throws PixImageException {
        Capture capture = captureEntry.capture();
        CaptureRegion region = captureEntry.capture().region;

        if (!capture.isSongRecordEmpty()) {
            capture.clearSongRecord();
        }

        try (PixImage pix = pixImageService.createPixImage(pngImage.data())) {
            // preprocessing
            pixImageService.preprocessCell(pix);

            // analyze
            for (RecordButton button : RecordButton.values()) {
                for (RecordPattern pattern : RecordPattern.values()) {
                    Rectangle rateRectangle = region.getRate(button, pattern);
                    Rectangle maxComboRectangle = region.getMaxCombo(button, pattern);

                    boolean maxCombo;
                    float rate;

                    try (PixImage recordPix = pix.crop(rateRectangle)) {
                        // test whether the image contains enough black pixels using the
                        // histogram. if true, run ocr.
                        float r = recordPix.getGrayRatio(RATE_FACTOR, RATE_THRESHOLD);
                        if (r < RATE_RATIO) {
                            continue;
                        }

                        String text = ocrService.run(recordPix);
                        text = CharMatcher.whitespace().removeFrom(text);

                        rate = parseRateText(text);
                        if (rate == -1) {
                            continue;
                        }
                    }

                    try (PixImage comboMarkPix = pix.crop(maxComboRectangle)) {
                        maxCombo =
                                comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR, COMBO_MARK_THRESHOLD)
                                        >= COMBO_MARK_RATIO;
                    }

                    capture.setSongRecord(button, pattern, new SongRecord(rate, maxCombo));
                }
            }
        }

        capture.setAnalyzed(true);
    }

    private float parseRateText(String text) {
        int index = text.indexOf('%');
        if (index == -1) {
            return -1;
        }

        try {
            String s = text.substring(0, index);
            float value = Float.parseFloat(s);
            return (value >= 0 && value <= 100) ? value : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private synchronized void increaseProgress() {
        updateProgress(++analyzedCaptureImageCount, queuedCaptureImageCount);
    }

    @Override
    protected Map<Integer, CaptureAnalysisTaskResult> callTask() throws Exception {
        if (captureEntryList.isEmpty()) {
            throw new IllegalArgumentException("captureEntryList is empty.");
        }

        updateProgress(0, 1);

        // prepare resultMap
        Map<Integer, CaptureAnalysisTaskResult> resultMap = captureEntryList.stream().collect(
                Collectors.toMap(CaptureEntry::entryId,
                        x -> new CaptureAnalysisTaskResult(x.entryId())));

        queuedCaptureImageCount = resultMap.size();

        // prepare a list for ocr services
        List<OcrService> ocrServiceList = new ArrayList<>(analyzerThreadCount);

        // start measuring task time
        Instant start = Instant.EPOCH;
        if (LOGGER.isTraceEnabled()) {
            start = Instant.now();
        }

        // run main tasks
        try {
            // initialize ocr services
            for (int i = 0; i < analyzerThreadCount; i++) {
                ocrServiceList.add(commonOcrServiceFactory.create());
            }

            // prepare an ExecutorService for analyzing
            try (ExecutorService analyzerExecutorService = Executors.newFixedThreadPool(
                    analyzerThreadCount)) {
                // prepare data structures
                BlockingQueue<Map.Entry<CaptureEntry, PngImage>> preloadedImageQueue =
                        new ArrayBlockingQueue<>(imagePreloaderQueueCapacity);

                // prepare an ExecutorService for image preloading
                try (ExecutorService imagePreloaderExecutorService = Executors.newFixedThreadPool(
                        imagePreloaderThreadCount)) {
                    // IMPORTANT NOTE
                    // If the following conditions are all met within this try block, this try block
                    // will never be terminated without an interrupt.
                    // - The preloadedImageQueue is full, and an element can no longer be added to
                    //   the queue.
                    // - All analyzer threads are terminated, and an element cannot be removed from
                    //   the queue.

                    // run image preloader
                    for (CaptureEntry entry : captureEntryList) {
                        if (entry.capture().isAnalyzed()) {
                            resultMap.get(entry.entryId())
                                    .setStatus(CaptureAnalysisTaskResult.Status.ALREADY_DONE);
                            increaseProgress();

                            continue;
                        }

                        imagePreloaderExecutorService.submit(() -> {
                            try {
                                PngImage pngImage = captureImageService.findById(entry.entryId());
                                preloadedImageQueue.put(Map.entry(entry, pngImage));
                            } catch (InterruptedException ignored) {
                            } catch (Exception e) {
                                LOGGER.atError().setCause(e).log("ImagePreloader Exception");
                                resultMap.get(entry.entryId()).setException(e);

                                increaseProgress();
                            }
                        });
                    }

                    // run analyzer (load balancing)
                    for (OcrService ocrService : ocrServiceList) { // NOPMD
                        analyzerExecutorService.submit(() -> {
                            try {
                                // this loop will break out by shutdownNow() invoked after emptying
                                // the preloadedImageQueue
                                while (true) {
                                    Map.Entry<CaptureEntry, PngImage> queueEntry =
                                            preloadedImageQueue.take();

                                    try {
                                        analyze(ocrService, queueEntry.getKey(),
                                                queueEntry.getValue());
                                        resultMap.get(queueEntry.getKey().entryId())
                                                .setStatus(CaptureAnalysisTaskResult.Status.DONE);
                                    } catch (Exception e) {
                                        LOGGER.atError().setCause(e).log("ImageAnalyzer Exception");
                                        resultMap.get(queueEntry.getKey().entryId())
                                                .setException(e);
                                    }

                                    increaseProgress();
                                }
                            } catch (InterruptedException ignored) {
                            }
                        });
                    }

                    // End of imagePreloaderExecutorService try block.
                    // The try block will wait for all tasks (image preloading) to be completed. If
                    // interrupted while waiting, shutdownNow() is invoked automatically, and then
                    // waiting again. For more information: {@link ExecutorService#close}
                }

                // wait for all preloaded images to start analyzing
                while (!preloadedImageQueue.isEmpty()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(250);
                    } catch (InterruptedException ignored) {
                    }
                }

                // terminate analyzer threads
                analyzerExecutorService.shutdownNow();

                // End of analyzerExecutorService try block.
                // The try block will wait for all tasks (analyzing) to be completed. If interrupted
                // while waiting, shutdownNow() is invoked automatically, and then waiting again.
                // For more information: {@link ExecutorService#close}
            }
        } finally {
            ocrServiceList.forEach(OcrService::close);
        }

        // stop measuring task time and log it
        if (LOGGER.isTraceEnabled()) {
            Instant end = Instant.now();

            LOGGER.trace("CaptureAnalysisTask Execution Time: {} [s]",
                    Duration.between(start, end).toMillis() / 1000.0);
        }

        return resultMap;
    }
}
