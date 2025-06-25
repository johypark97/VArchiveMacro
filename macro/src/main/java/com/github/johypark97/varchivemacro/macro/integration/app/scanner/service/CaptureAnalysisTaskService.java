package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.CaptureAnalysisTaskResult;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public interface CaptureAnalysisTaskService {
    Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(List<CaptureEntry> captureEntryList)
            throws IOException;

    boolean stopTask();
}
