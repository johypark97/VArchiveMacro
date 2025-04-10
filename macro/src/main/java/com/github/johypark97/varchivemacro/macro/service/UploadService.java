package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import java.util.List;
import javafx.concurrent.Task;

public interface UploadService {
    Task<Void> createTask_collectNewRecord();

    Task<Void> createTask_startUpload();

    void stopTask_upload();

    boolean isNewRecordDataEmpty();

    List<NewRecordData> copyNewRecordDataList();
}
