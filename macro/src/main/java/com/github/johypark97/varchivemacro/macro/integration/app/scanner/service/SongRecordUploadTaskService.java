package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.SongRecordUploadTaskResult;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public interface SongRecordUploadTaskService {
    Task<Map<Integer, SongRecordUploadTaskResult>> createTask(List<Integer> selectedEntryIdList);

    boolean stopTask();
}
