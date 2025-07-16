package com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public class ScannerAnalysisService {
    private final CaptureImageService captureImageService;
    private final ConfigService configService;
    private final PixImageService pixImageService;

    private final OcrServiceFactory commonOcrServiceFactory;

    public ScannerAnalysisService(CaptureImageService captureImageService,
            ConfigService configService, PixImageService pixImageService,
            OcrServiceFactory commonOcrServiceFactory) {
        this.captureImageService = captureImageService;
        this.configService = configService;
        this.pixImageService = pixImageService;

        this.commonOcrServiceFactory = commonOcrServiceFactory;
    }

    public Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(
            List<CaptureEntry> captureEntryList) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configService.findScannerConfig();

        return TaskManager.getInstance().register(CaptureAnalysisTask.class,
                new CaptureAnalysisTask(captureImageService, pixImageService,
                        commonOcrServiceFactory, config, captureEntryList));
    }

    public boolean stopTask() {
        return TaskManager.Helper.cancel(CaptureAnalysisTask.class);
    }
}
