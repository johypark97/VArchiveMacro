package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.macro.core.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.macro.core.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.scanner.ScannerTask.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionAreaFactory;
import com.google.common.base.CharMatcher;
import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisService.class);

    public static final float COMBO_MARK_RATIO = 0.01f;
    public static final float RATE_RATIO = 0.01f;
    public static final int COMBO_MARK_FACTOR = 8;
    public static final int COMBO_MARK_THRESHOLD = 224;
    public static final int RATE_FACTOR = 8;
    public static final int RATE_THRESHOLD = 224;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public Exception exception;

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void await() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    public void execute(ScannerTaskManager taskManager) {
        executor.execute(() -> {
            try (OcrWrapper ocr = new OcrWrapper()) {
                List<ScannerTask> queue =
                        taskManager.getTasks().stream().filter((x) -> x.getException() == null)
                                .toList();

                queue.forEach((x) -> x.setStatus(ScannerTaskStatus.WAITING));

                Thread thread = Thread.currentThread();
                for (ScannerTask task : queue) {
                    analyze(ocr, task);

                    if (thread.isInterrupted()) {
                        break;
                    }
                }
            } catch (OcrInitializationError ignored) {
            }
        });
        executor.shutdown();
    }

    public static void analyze(OcrWrapper ocr, ScannerTask task) {
        task.setStatus(ScannerTaskStatus.ANALYZING);
        task.clearAnalyzedData();

        try (PixWrapper pix = new PixWrapper(task.filePath)) {
            Dimension size = new Dimension(pix.getWidth(), pix.getHeight());
            CollectionArea area = CollectionAreaFactory.create(size);

            // -------- preprocessing --------
            pix.convertRGBToLuminance();
            pix.gammaTRC(0.2f, 0, 255);
            pix.invert();

            // -------- analyze records --------
            for (Button button : Button.values()) {
                for (Pattern pattern : Pattern.values()) {
                    AnalyzedData data;
                    CollectionArea.Button b = button.toCollectionArea();
                    CollectionArea.Pattern p = pattern.toCollectionArea();

                    try (PixWrapper recordPix = pix.crop(area.getRate(b, p))) {
                        // test whether the image contains enough black pixels using the histogram.
                        // if true, run ocr.
                        float r = recordPix.getGrayRatio(RATE_FACTOR, RATE_THRESHOLD);
                        if (r < RATE_RATIO) {
                            continue;
                        }

                        String text = ocr.run(recordPix.pixInstance);
                        text = CharMatcher.whitespace().removeFrom(text);

                        data = new AnalyzedData();
                        data.rate = parseRateText(text);
                        data.rateText = text;
                    }

                    try (PixWrapper comboMarkPix = pix.crop(area.getComboMark(b, p))) {
                        float r =
                                comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR, COMBO_MARK_THRESHOLD);
                        data.isMaxCombo = r >= COMBO_MARK_RATIO;
                    }

                    task.addAnalyzedData(button, pattern, data);
                }
            }

            task.setStatus(ScannerTaskStatus.ANALYZED);
        } catch (Exception e) {
            task.setException(e);
            LOGGER.atError().log(e.getMessage(), e);
        }
    }

    public static float parseRateText(String text) {
        int index = text.indexOf('%');
        if (index == -1) {
            return -1;
        }

        try {
            String s = text.substring(0, index);
            float value = Float.parseFloat(s);
            return (value >= 0 && value <= 100) ? value : -1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }
}
