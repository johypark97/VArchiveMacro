package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.macro.application.scanner.model.CaptureAnalysisTaskResult;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureEntry;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public interface CaptureAnalysisTaskService {
    Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(List<CaptureEntry> captureEntryList)
            throws IOException;

    boolean stopTask();
}
