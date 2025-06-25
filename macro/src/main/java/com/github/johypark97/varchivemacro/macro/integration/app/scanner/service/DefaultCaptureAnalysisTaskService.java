package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.cache.infra.CaptureImageCache;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.CaptureAnalysisTask;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public class DefaultCaptureAnalysisTaskService implements CaptureAnalysisTaskService {
    private final ConfigRepository configRepository;

    private final CaptureImageCacheFactory captureImageCacheFactory;
    private final OcrFactory commonOcrFactory;

    public DefaultCaptureAnalysisTaskService(ConfigRepository configRepository,
            CaptureImageCacheFactory captureImageCacheFactory, OcrFactory commonOcrFactory) {
        this.captureImageCacheFactory = captureImageCacheFactory;
        this.commonOcrFactory = commonOcrFactory;
        this.configRepository = configRepository;
    }

    @Override
    public Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(
            List<CaptureEntry> captureEntryList) throws IOException {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configRepository.findScannerConfig();

        CaptureImageCache captureImageCache =
                captureImageCacheFactory.create(config.cacheDirectory());

        return TaskManager.getInstance().register(CaptureAnalysisTask.class,
                new CaptureAnalysisTask(captureImageCache, commonOcrFactory, config,
                        captureEntryList));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(CaptureAnalysisTask.class);
    }
}
