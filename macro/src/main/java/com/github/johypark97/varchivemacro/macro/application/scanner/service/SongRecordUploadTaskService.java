package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.macro.application.scanner.model.SongRecordUploadTaskResult;
import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;

public interface SongRecordUploadTaskService {
    Task<Map<Integer, SongRecordUploadTaskResult>> createTask(List<Integer> selectedEntryIdList)
            throws Exception;

    boolean stopTask();
}
