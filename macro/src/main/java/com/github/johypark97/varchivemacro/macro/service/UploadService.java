package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.NewRecordData;
import java.util.List;
import javafx.concurrent.Task;

public interface UploadService {
    boolean isReady_collectNewRecord();

    Task<Void> createTask_collectNewRecord();

    boolean isReady_upload();

    Task<Void> createTask_startUpload();

    void stopTask_upload();

    List<NewRecordData> copyNewRecordDataList();
}
