package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.Enums;
import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixPreprocessor;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.ScannerOcr;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.DefaultTaskManager.DefaultAnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import com.google.common.base.CharMatcher;
import java.io.IOException;
import java.util.LinkedList;
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

    public void execute(TaskManager taskManager) {
        executor.execute(() -> {
            try (OcrWrapper ocr = new ScannerOcr()) {
                List<TaskData> queue = new LinkedList<>();
                for (TaskData task : taskManager) {
                    if (!task.hasException() && task.isValid()) {
                        queue.add(task);
                        task.setStatus(TaskStatus.WAITING);
                    }
                }

                Thread thread = Thread.currentThread();
                for (TaskData task : queue) {
                    analyze(ocr, task);

                    if (thread.isInterrupted()) {
                        break;
                    }
                }
            } catch (OcrInitializationError e) {
                LOGGER.atError().log(e.getMessage(), e);
                exception = e;
            }
        });
        executor.shutdown();
    }

    public void analyze(OcrWrapper ocr, TaskData task) {
        task.setStatus(TaskStatus.ANALYZING);
        task.clearAnalyzedData();

        try (PixWrapper pix = new PixWrapper(task.getImagePath())) {
            CollectionArea area = task.getCollectionArea();

            // -------- preprocessing --------
            PixPreprocessor.preprocessCell(pix);

            // -------- analyze records --------
            for (Button button : Button.values()) {
                for (Pattern pattern : Pattern.values()) {
                    AnalyzedData data;
                    Enums.Button b = button.toLib();
                    Enums.Pattern p = pattern.toLib();

                    try (PixWrapper recordPix = pix.crop(area.getRate(b, p))) {
                        // test whether the image contains enough black pixels using the histogram.
                        // if true, run ocr.
                        float r = recordPix.getGrayRatio(RATE_FACTOR, RATE_THRESHOLD);
                        if (r < RATE_RATIO) {
                            continue;
                        }

                        String text = ocr.run(recordPix.pixInstance);
                        text = CharMatcher.whitespace().removeFrom(text);

                        data = new DefaultAnalyzedData();
                        data.setRateText(text);
                    }

                    try (PixWrapper comboMarkPix = pix.crop(area.getComboMark(b, p))) {
                        float r =
                                comboMarkPix.getGrayRatio(COMBO_MARK_FACTOR, COMBO_MARK_THRESHOLD);
                        data.setMaxCombo(r >= COMBO_MARK_RATIO);
                    }

                    task.addAnalyzedData(button, pattern, data);
                }
            }

            task.setStatus(TaskStatus.ANALYZED);
        } catch (IOException | PixError e) {
            task.setException(e);
            LOGGER.atError().log(e.getMessage(), e);
        }
    }
}
