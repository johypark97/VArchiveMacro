package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.ImageConverter;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.CacheManager;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;
import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.AnalysisData.Status;
import com.github.johypark97.varchivemacro.macro.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.model.RecordData;
import com.github.johypark97.varchivemacro.macro.service.ocr.ScannerOcr;
import com.google.common.base.CharMatcher;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisTask extends InterruptibleTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisTask.class);

    private static final float COMBO_MARK_RATIO = 0.01f;
    private static final float RATE_RATIO = 0.01f;
    private static final int COMBO_MARK_FACTOR = 8;
    private static final int COMBO_MARK_THRESHOLD = 224;
    private static final int RATE_FACTOR = 8;
    private static final int RATE_THRESHOLD = 224;

    private final Runnable onDataReady;
    private final String cacheDirectory;
    private final int analyzerThreadCount;
    private final int imagePreloaderThreadCount;
    private final int imagePreloadingLimit;

    private final WeakReference<AnalysisDataDomain> analysisDataDomainReference;
    private final WeakReference<ScanDataDomain> scanDataDomainReference;

    private CacheManager cacheManager;
    private int completedTaskCount;
    private int totalTaskCount;

    public AnalysisTask(Runnable onDataReady, ScanDataDomain scanDataDomain,
            AnalysisDataDomain analysisDataDomain, String cacheDirectory, int threadCount) {
        this.cacheDirectory = cacheDirectory;
        this.onDataReady = onDataReady;

        analyzerThreadCount = threadCount;
        imagePreloaderThreadCount = (int) (Math.log(threadCount + 1) / Math.log(2));
        imagePreloadingLimit = threadCount * 3 / 2;

        analysisDataDomainReference = new WeakReference<>(analysisDataDomain);
        scanDataDomainReference = new WeakReference<>(scanDataDomain);
    }

    private AnalysisDataDomain getAnalysisDataDomain() {
        return analysisDataDomainReference.get();
    }

    private ScanDataDomain getScanDataDomain() {
        return scanDataDomainReference.get();
    }

    private byte[] loadImage(AnalysisData analysisData) throws IOException {
        return ImageConverter.imageToPngBytes(
                cacheManager.read(analysisData.captureData.get().idProperty().get()));
    }

    private void analyze(OcrWrapper ocr, CollectionArea area, AnalysisData analysisData,
            byte[] pngBytes) throws PixError {
        analysisData.status.set(Status.ANALYZING);

        try (PixWrapper pix = new PixWrapper(pngBytes)) {
            // preprocessing
            PixPreprocessor.preprocessCell(pix);

            // analyze
            for (Button button : Button.values()) {
                for (Pattern pattern : Pattern.values()) {
                    RecordData recordData = new RecordData();

                    try (PixWrapper recordPix = pix.crop(area.getRate(button, pattern))) {
                        // test whether the image contains enough black pixels using the
                        // histogram. if true, run ocr.
                        float r = recordPix.getGrayRatio(RATE_FACTOR, RATE_THRESHOLD);
                        if (r < RATE_RATIO) {
                            continue;
                        }

                        String text = ocr.run(recordPix.pixInstance);
                        text = CharMatcher.whitespace().removeFrom(text);

                        recordData.rateText.set(text);
                    }

                    try (PixWrapper comboMarkPix = pix.crop(area.getComboMark(button, pattern))) {
                        float r =
                                comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR, COMBO_MARK_THRESHOLD);
                        recordData.maxCombo.set(r >= COMBO_MARK_RATIO);
                    }

                    analysisData.recordDataTable.put(button, pattern, recordData);
                }
            }

            analysisData.status.set(Status.DONE);
        }
    }

    private void increaseProgress() {
        synchronized (this) {
            updateProgress(++completedTaskCount, totalTaskCount);
        }
    }

    @Override
    protected Void callTask() throws Exception {
        Instant start = Instant.now();

        // throw an exception if there is no scan data
        if (getScanDataDomain().isEmpty()) {
            throw new IllegalStateException("ScanDataDomain is empty");
        }

        // throw an exception if there are previous analysis data
        if (!getAnalysisDataDomain().isEmpty()) {
            throw new IllegalStateException("AnalysisDataDomain is not clean");
        }

        cacheManager = new CacheManager(cacheDirectory);

        updateProgress(0, 1);

        // prepare AnalysisData and filter out those that are not suitable for analysis
        List<AnalysisData> dataList = new LinkedList<>();
        getScanDataDomain().copySongDataList().stream().filter(x -> x.selected.get()).forEach(x -> {
            AnalysisData analysisData = getAnalysisDataDomain().createAnalysisData(x);

            if (x.childListProperty().isEmpty()) {
                analysisData.setException(
                        new IllegalArgumentException("There are no linked captures."));
                return;
            } else if (x.childListProperty().size() > 1) {
                analysisData.setException(
                        new IllegalArgumentException("Too many linked captures."));
                return;
            }

            CaptureData captureData = x.childListProperty().get(0);
            if (captureData.exception.get() != null) {
                analysisData.setException(
                        new IllegalArgumentException("An exception exists in capture."));
                return;
            }

            analysisData.captureData.set(captureData);
            dataList.add(analysisData);
        });
        totalTaskCount = dataList.size();
        onDataReady.run();

        // prepare CollectionArea using the first capture image
        CollectionArea area;
        {
            AnalysisData captureData = dataList.get(0);
            BufferedImage image = cacheManager.read(captureData.idProperty().get());

            Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
            area = CollectionAreaFactory.create(resolution);
        }

        // run tasks
        List<CompletableFuture<Void>> cancelableFutureList = new ArrayList<>();
        List<CompletableFuture<Void>> imagePreoaderFutureList = new ArrayList<>();

        BlockingQueue<Entry<AnalysisData, byte[]>> preloadedImageQueue =
                new ArrayBlockingQueue<>(imagePreloadingLimit);

        ExecutorService imagePreloaderExecutorService; // NOPMD
        imagePreloaderExecutorService = Executors.newFixedThreadPool(imagePreloaderThreadCount);

        // run image preloader
        dataList.forEach(analysisData -> {
            CompletableFuture<Void> cancelableFuture = CompletableFuture.runAsync(() -> {
            }, imagePreloaderExecutorService);
            cancelableFutureList.add(cancelableFuture);

            CompletableFuture<Void> imagePreloaderFuture = cancelableFuture.thenRun(() -> {
                try {
                    analysisData.status.set(Status.LOADING);
                    preloadedImageQueue.put(Map.entry(analysisData, loadImage(analysisData)));
                    analysisData.status.set(Status.WAITING);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    if (throwable instanceof CompletionException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof CancellationException) {
                            analysisData.status.set(Status.CANCELED);
                        } else {
                            LOGGER.atError().setCause(e).log();
                            analysisData.setException(e);
                        }
                    } else {
                        LOGGER.atError().setCause(throwable)
                                .log("Unexpected AnalysisTask exception");
                        analysisData.setException(new Exception(throwable));
                    }
                }
            });
            imagePreoaderFutureList.add(imagePreloaderFuture);
        });
        imagePreloaderExecutorService.shutdown();

        // run analyzer (load balancing)
        ExecutorService analyzerExecutorService; // NOPMD
        analyzerExecutorService = Executors.newFixedThreadPool(analyzerThreadCount);

        for (int i = 0; i < analyzerThreadCount; i++) {
            analyzerExecutorService.submit(() -> {
                try (OcrWrapper ocr = new ScannerOcr()) {
                    while (!Thread.currentThread().isInterrupted()) {
                        Entry<AnalysisData, byte[]> entry = preloadedImageQueue.take();

                        AnalysisData analysisData = entry.getKey();
                        byte[] pngBytes = entry.getValue();

                        try {
                            analyze(ocr, area, analysisData, pngBytes);
                            increaseProgress();
                        } catch (Exception e) {
                            LOGGER.atError().setCause(e).log();
                            analysisData.setException(e);
                        }
                    }
                } catch (OcrInitializationError e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException ignored) {
                }
            });
        }
        analyzerExecutorService.shutdown();

        // wait for image preloading tasks to be complete or cancel it if interrupted
        while (true) {
            try {
                CompletableFuture.allOf(imagePreoaderFutureList.toArray(CompletableFuture[]::new))
                        .get();
                break;
            } catch (InterruptedException e) {
                cancelableFutureList.forEach(x -> x.cancel(false));
            } catch (Exception e) {
                break;
            }
        }

        // wait for image preloading tasks that have already started, to be moved to the analysis task.
        while (!preloadedImageQueue.isEmpty()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        // terminate analyzer threads
        analyzerExecutorService.shutdownNow();

        // wait for analyzer threads to complete analysis tasks and to be terminated
        while (true) {
            try {
                if (!analyzerExecutorService.awaitTermination(1, TimeUnit.DAYS)) {
                    throw new AssertionError("Unexpected timeout");
                }

                break;
            } catch (InterruptedException ignored) {
            }
        }

        if (LOGGER.isInfoEnabled()) {
            Instant end = Instant.now();
            LOGGER.info("AnalysisTask Execution Time: {} [s]",
                    Duration.between(start, end).toMillis() / 1000.0);
        }

        return null;
    }
}
