package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CaptureAnalysisTask;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public class DefaultCaptureAnalysisTaskService implements CaptureAnalysisTaskService {
    private final CaptureImageService captureImageService;
    private final ConfigService configService;

    private final OcrServiceFactory commonOcrServiceFactory;

    public DefaultCaptureAnalysisTaskService(CaptureImageService captureImageService,
            ConfigService configService, OcrServiceFactory commonOcrServiceFactory) {
        this.captureImageService = captureImageService;
        this.configService = configService;

        this.commonOcrServiceFactory = commonOcrServiceFactory;
    }

    @Override
    public Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(
            List<CaptureEntry> captureEntryList) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configService.findScannerConfig();

        return TaskManager.getInstance().register(CaptureAnalysisTask.class,
                new CaptureAnalysisTask(captureImageService, commonOcrServiceFactory, config,
                        captureEntryList));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CaptureAnalysisTask.class);
    }
}
