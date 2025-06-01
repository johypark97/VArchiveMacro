package com.github.johypark97.varchivemacro.macro.application.scanner.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.application.common.InterruptibleTask;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.model.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.converter.CaptureBoundConverter;
import com.github.johypark97.varchivemacro.macro.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Capture;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureArea;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureBound;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.infrastructure.cache.CaptureImageCache;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.google.common.base.CharMatcher;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
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

    private final CaptureImageCache captureImageCache;
    private final OcrFactory commonOcrFactory;
    private final int analyzerThreadCount;
    private final int imagePreloaderQueueCapacity;
    private final int imagePreloaderThreadCount;

    private final List<CaptureEntry> captureEntryList;

    public CaptureAnalysisTask(CaptureImageCache captureImageCache, OcrFactory commonOcrFactory,
            ScannerConfig config, List<CaptureEntry> captureEntryList) {
        this.captureEntryList = captureEntryList;
        this.captureImageCache = captureImageCache;
        this.commonOcrFactory = commonOcrFactory;

        analyzerThreadCount = Math.max(1, config.analyzerThreadCount());
        imagePreloaderQueueCapacity = analyzerThreadCount * 3 / 2;
        imagePreloaderThreadCount = (int) (Math.log(analyzerThreadCount + 1) / Math.log(2));
    }

    private void analyze(OcrWrapper ocr, CollectionArea area, CaptureEntry captureEntry,
            byte[] pngByte) throws PixError {
        Capture capture = captureEntry.capture();

        if (!capture.isCaptureAreaEmpty()) {
            capture.clearCaptureAreaEmpty();
        }

        try (PixWrapper pix = new PixWrapper(pngByte)) {
            // preprocessing
            PixPreprocessor.preprocessCell(pix);

            // analyze
            for (RecordButton button : RecordButton.values()) {
                Enums.Button libButton = RecordButtonConverter.toLib(button);

                for (RecordPattern pattern : RecordPattern.values()) {
                    Enums.Pattern libPattern = RecordPatternConverter.toLib(pattern);

                    CaptureBound maxComboBound = CaptureBoundConverter.fromRectangle(
                            area.getComboMark(libButton, libPattern));
                    CaptureBound rateBound = CaptureBoundConverter.fromRectangle(
                            area.getRate(libButton, libPattern));

                    boolean maxCombo;
                    float rate;

                    try (PixWrapper recordPix = pix.crop(
                            CaptureBoundConverter.toRectangle(rateBound))) {
                        // test whether the image contains enough black pixels using the
                        // histogram. if true, run ocr.
                        float r = recordPix.getGrayRatio(RATE_FACTOR, RATE_THRESHOLD);
                        if (r < RATE_RATIO) {
                            continue;
                        }

                        String text = ocr.run(recordPix.pixInstance);
                        text = CharMatcher.whitespace().removeFrom(text);

                        rate = parseRateText(text);
                        if (rate == -1) {
                            continue;
                        }
                    }

                    try (PixWrapper comboMarkPix = pix.crop(
                            CaptureBoundConverter.toRectangle(maxComboBound))) {
                        maxCombo =
                                comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR, COMBO_MARK_THRESHOLD)
                                        >= COMBO_MARK_RATIO;
                    }

                    SongRecord record = new SongRecord(rate, maxCombo);
                    CaptureArea captureArea = new CaptureArea(record, rateBound, maxComboBound);

                    capture.setCaptureArea(button, pattern, captureArea);
                }
            }
        }
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

    @Override
    protected Map<Integer, CaptureAnalysisTaskResult> callTask() throws Exception {
        if (captureEntryList.isEmpty()) {
            throw new IllegalArgumentException("captureEntryList is empty.");
        }

        // prepare CollectionArea using the first capture image
        CollectionArea area;
        {
            int id = captureEntryList.getFirst().entryId();
            BufferedImage image = captureImageCache.read(id);
            Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
            area = CollectionAreaFactory.create(resolution);
        }

        // prepare resultMap
        Map<Integer, CaptureAnalysisTaskResult> resultMap = captureEntryList.stream().collect(
                Collectors.toMap(CaptureEntry::entryId,
                        x -> new CaptureAnalysisTaskResult(x.entryId())));

        // prepare a list for ocr instances
        List<OcrWrapper> ocrInstanceList = new ArrayList<>(analyzerThreadCount);

        // start measuring task time
        Instant start = Instant.EPOCH;
        if (LOGGER.isTraceEnabled()) {
            start = Instant.now();
        }

        // run main tasks
        try {
            // initialize ocr instances
            for (int i = 0; i < analyzerThreadCount; i++) {
                ocrInstanceList.add(commonOcrFactory.create());
            }

            // prepare an ExecutorService for analyzing
            try (ExecutorService analyzerExecutorService = Executors.newFixedThreadPool(
                    analyzerThreadCount)) {
                // prepare data structures
                BlockingQueue<Map.Entry<CaptureEntry, byte[]>> preloadedImageQueue =
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
                        imagePreloaderExecutorService.submit(() -> {
                            try {
                                BufferedImage image = captureImageCache.read(entry.entryId());
                                byte[] imagePngByte = ImageConverter.imageToPngBytes(image);
                                preloadedImageQueue.put(Map.entry(entry, imagePngByte));
                            } catch (InterruptedException ignored) {
                            } catch (Exception e) {
                                LOGGER.atError().setCause(e).log("ImagePreloader Exception");
                                resultMap.get(entry.entryId()).setException(e);
                            }
                        });
                    }

                    // run analyzer (load balancing)
                    for (OcrWrapper ocr : ocrInstanceList) { // NOPMD
                        analyzerExecutorService.submit(() -> {
                            try {
                                // this loop will break out by shutdownNow() invoked after emptying
                                // the preloadedImageQueue
                                while (true) {
                                    Map.Entry<CaptureEntry, byte[]> queueEntry =
                                            preloadedImageQueue.take();

                                    try {
                                        analyze(ocr, area, queueEntry.getKey(),
                                                queueEntry.getValue());
                                        resultMap.get(queueEntry.getKey().entryId())
                                                .setStatus(CaptureAnalysisTaskResult.Status.DONE);
                                    } catch (Exception e) {
                                        LOGGER.atError().setCause(e).log("ImageAnalyzer Exception");
                                        resultMap.get(queueEntry.getKey().entryId())
                                                .setException(e);
                                    }
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
            ocrInstanceList.forEach(OcrWrapper::close);
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
