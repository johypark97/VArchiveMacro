package com.github.johypark97.varchivemacro.macro.fxgui.model.service.task;

import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasMany;

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
import java.util.Queue;

public class AnalysisTask extends InterruptibleTask<Void> {
    private static final float COMBO_MARK_RATIO = 0.01f;
    private static final float RATE_RATIO = 0.01f;
    private static final int COMBO_MARK_FACTOR = 8;
    private static final int COMBO_MARK_THRESHOLD = 224;
    private static final int RATE_FACTOR = 8;
    private static final int RATE_THRESHOLD = 224;

    private final CacheManager cacheManager;
    private final Runnable onDataReady;

    private final WeakReference<AnalysisDataManager> analysisDataManagerReference;
    private final WeakReference<ScanDataManager> scanDataManagerReference;

    private CollectionArea area;

    public AnalysisTask(Runnable onDataReady, ScanDataManager scanDataManager,
            AnalysisDataManager analysisDataManager, Path cacheDirectoryPath) {
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

    private void analyze(OcrWrapper ocr, AnalysisData analysisData) {
        analysisData.status.set(Status.ANALYZING);

        CaptureData captureData = analysisData.captureData.get();

        try {
            if (area == null) {
                BufferedImage image = cacheManager.read(captureData.idProperty().get());

                Dimension resolution = new Dimension(image.getWidth(), image.getHeight());
                area = CollectionAreaFactory.create(resolution);
            }

            try (PixWrapper pix = PixWrapper.load(
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

                        try (PixWrapper comboMarkPix = pix.crop(
                                area.getComboMark(button, pattern))) {
                            float r = comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR,
                                    COMBO_MARK_THRESHOLD);
                            recordData.maxCombo.set(r >= COMBO_MARK_RATIO);
                        }

                        analysisData.recordDataTable.put(button, pattern, recordData);
                    }
                }
            }

            analysisData.status.set(Status.DONE);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            analysisData.setException(e);
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

        Queue<AnalysisData> queue = new LinkedList<>();
        getScanDataManager().copySongDataList().stream().filter(x -> x.selected.get())
                .forEach(x -> {
                    AnalysisData analysisData = getAnalysisDataManager().createAnalysisData(x);

                    if (x.childListProperty().isEmpty()) {
                        analysisData.setException(
                                new IllegalArgumentException("There are no linked captures."));
                        return;
                    } else if (hasMany(x.childListProperty())) {
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
                    queue.add(analysisData);
                });
        onDataReady.run();

        try (OcrWrapper ocr = ScannerOcr.load()) {
            while (queue.peek() != null) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                AnalysisData analysisData = queue.poll();
                analyze(ocr, analysisData);
            }
        } catch (InterruptedException e) {
            queue.forEach(x -> x.status.set(Status.CANCELED));
        }

        return null;
    }
}
