package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.application.scanner.task.CollectNewRecordTask;
import com.github.johypark97.varchivemacro.macro.application.scanner.task.UploadTask;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.NewRecordData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.AnalysisDataRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.database.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.record.repository.RecordRepository;
import java.util.List;
import javafx.concurrent.Task;

public class DefaultUploadService implements UploadService {
    private final AnalysisDataRepository analysisDataRepository;
    private final ConfigRepository configRepository;
    private final DatabaseRepository databaseRepository;
    private final NewRecordDataRepository newRecordDataRepository;
    private final RecordRepository recordRepository;

    public DefaultUploadService(AnalysisDataRepository analysisDataRepository,
            ConfigRepository configRepository, DatabaseRepository databaseRepository,
            NewRecordDataRepository newRecordDataRepository, RecordRepository recordRepository) {
        this.analysisDataRepository = analysisDataRepository;
        this.configRepository = configRepository;
        this.databaseRepository = databaseRepository;
        this.newRecordDataRepository = newRecordDataRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    public boolean isReady_collectNewRecord() {
        return !analysisDataRepository.isEmpty();
    }

    @Override
    public Task<Void> createTask_collectNewRecord() {
        return TaskManager.getInstance().register(CollectNewRecordTask.class,
                new CollectNewRecordTask(analysisDataRepository, newRecordDataRepository,
                        recordRepository));
    }

    @Override
    public boolean isReady_upload() {
        return !newRecordDataRepository.isEmpty();
    }

    @Override
    public Task<Void> createTask_startUpload() {
        ScannerConfig config = configRepository.getScannerConfig();

        return TaskManager.getInstance().register(UploadTask.class,
                new UploadTask(databaseRepository, newRecordDataRepository, recordRepository,
                        config.accountFile, config.recordUploadDelay));
    }

    @Override
    public void stopTask_upload() {
        TaskManager.Helper.cancel(UploadTask.class);
    }

    @Override
    public List<NewRecordData> copyNewRecordDataList() {
        return newRecordDataRepository.copyNewRecordDataList();
    }
}
