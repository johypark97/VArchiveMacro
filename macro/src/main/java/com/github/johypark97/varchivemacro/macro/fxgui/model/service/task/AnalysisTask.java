package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.scanner.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData.Status;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.RecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.CacheManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ocr.ScannerOcr;
import com.google.common.base.CharMatcher;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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

    private final CacheManager cacheManager;
    private final Runnable onDataReady;
    private final int analysisThreadCount;

    private final WeakReference<AnalysisDataManager> analysisDataManagerReference;
    private final WeakReference<ScanDataManager> scanDataManagerReference;

    private int completedTaskCount;
    private int totalTaskCount;

    public AnalysisTask(Runnable onDataReady, ScanDataManager scanDataManager,
            AnalysisDataManager analysisDataManager, Path cacheDirectoryPath,
            int analysisThreadCount) {
        this.analysisThreadCount = analysisThreadCount;
        this.onDataReady = onDataReady;

        cacheManager = new CacheManager(cacheDirectoryPath);

        analysisDataManagerReference = new WeakReference<>(analysisDataManager);
        scanDataManagerReference = new WeakReference<>(scanDataManager);
    }

    private AnalysisDataManager getAnalysisDataManager() {
        return analysisDataManagerReference.get();
    }

    private ScanDataManager getScanDataManager() {
        return scanDataManagerReference.get();
    }

    private void analyze(OcrWrapper ocr, CollectionArea area, AnalysisData analysisData) {
        analysisData.status.set(Status.ANALYZING);

        CaptureData captureData = analysisData.captureData.get();

        try (PixWrapper pix = new PixWrapper(
                cacheManager.createPath(captureData.idProperty().get()))) {
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
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log();
            analysisData.setException(e);
        }
    }

    private void increaseProgress() {
        synchronized (this) {
            updateProgress(++completedTaskCount, totalTaskCount);
        }
    }

    @Override
    protected Void callTask() throws Exception {
        // throw an exception if there is no scan data
        if (getScanDataManager().isEmpty()) {
            throw new IllegalStateException("ScanDataManager is empty");
        }

        // throw an exception if there are previous analysis data
        if (!getAnalysisDataManager().isEmpty()) {
            throw new IllegalStateException("AnalysisDataManager is not clean");
        }

        updateProgress(0, 1);

        List<AnalysisData> dataList = new LinkedList<>();
        getScanDataManager().copySongDataList().stream().filter(x -> x.selected.get())
                .forEach(x -> {
                    AnalysisData analysisData = getAnalysisDataManager().createAnalysisData(x);

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

        CollectionArea area;
        {
            AnalysisData captureData = dataList.get(0);
            BufferedImage image = cacheManager.read(captureData.idProperty().get());

            Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
            area = CollectionAreaFactory.create(resolution);
        }

        Map<Future<?>, AnalysisData> futureAnalysisDataMap;

        try (OcrWrapper ocr = new ScannerOcr()) {
            ExecutorService executorService = Executors.newFixedThreadPool(analysisThreadCount);
            futureAnalysisDataMap =
                    dataList.stream().collect(Collectors.toMap(x -> executorService.submit(() -> {
                        analyze(ocr, area, x);
                        increaseProgress();
                    }), x -> x));
            executorService.shutdown();

            while (true) {
                try {
                    if (!executorService.awaitTermination(1, TimeUnit.DAYS)) {
                        throw new AssertionError("Unexpected timeout");
                    }

                    break;
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                }
            }
        }

        futureAnalysisDataMap.forEach((future, analysisData) -> {
            if (!future.isDone()) {
                analysisData.status.set(Status.CANCELED);
            }
        });

        return null;
    }
}
