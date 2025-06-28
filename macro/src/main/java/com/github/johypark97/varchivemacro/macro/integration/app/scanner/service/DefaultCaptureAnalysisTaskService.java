package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CaptureAnalysisTask;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public class DefaultCaptureAnalysisTaskService implements CaptureAnalysisTaskService {
    private final CaptureImageRepository captureImageRepository;
    private final ConfigRepository configRepository;

    private final OcrFactory commonOcrFactory;

    public DefaultCaptureAnalysisTaskService(CaptureImageRepository captureImageRepository,
            ConfigRepository configRepository, OcrFactory commonOcrFactory) {
        this.captureImageRepository = captureImageRepository;
        this.configRepository = configRepository;

        this.commonOcrFactory = commonOcrFactory;
    }

    @Override
    public Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(
            List<CaptureEntry> captureEntryList) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configRepository.findScannerConfig();

        return TaskManager.getInstance().register(CaptureAnalysisTask.class,
                new CaptureAnalysisTask(captureImageRepository, commonOcrFactory, config,
                        captureEntryList));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CaptureAnalysisTask.class);
    }
}
