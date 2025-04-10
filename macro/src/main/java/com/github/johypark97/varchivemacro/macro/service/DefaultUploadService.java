package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import com.github.johypark97.varchivemacro.macro.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import com.github.johypark97.varchivemacro.macro.service.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.service.task.UploadTask;
import java.util.List;
import javafx.concurrent.Task;

public class DefaultUploadService implements UploadService {
    private final ConfigRepository configRepository;
    private final DatabaseRepository databaseRepository;
    private final RecordRepository recordRepository;

    private final AnalysisDataDomain analysisDataDomain;
    private final NewRecordDataDomain newRecordDataDomain;

    public DefaultUploadService(ConfigRepository configRepository,
            DatabaseRepository databaseRepository, RecordRepository recordRepository,
            AnalysisDataDomain analysisDataDomain, NewRecordDataDomain newRecordDataDomain) {
        this.analysisDataDomain = analysisDataDomain;
        this.configRepository = configRepository;
        this.databaseRepository = databaseRepository;
        this.newRecordDataDomain = newRecordDataDomain;
        this.recordRepository = recordRepository;
    }

    @Override
    public boolean isReady_collectNewRecord() {
        return !analysisDataDomain.isEmpty();
    }

    @Override
    public Task<Void> createTask_collectNewRecord() {
        return TaskManager.getInstance().register(CollectNewRecordTask.class,
                new CollectNewRecordTask(recordRepository, analysisDataDomain,
                        newRecordDataDomain));
    }

    @Override
    public boolean isReady_upload() {
        return !newRecordDataDomain.isEmpty();
    }

    @Override
    public Task<Void> createTask_startUpload() {
        ScannerConfig config = configRepository.getScannerConfig();

        return TaskManager.getInstance().register(UploadTask.class,
                new UploadTask(databaseRepository, recordRepository, newRecordDataDomain,
                        config.accountFile, config.recordUploadDelay));
    }

    @Override
    public void stopTask_upload() {
        TaskManager.Helper.cancel(UploadTask.class);
    }

    @Override
    public List<NewRecordData> copyNewRecordDataList() {
        return newRecordDataDomain.copyNewRecordDataList();
    }
}
