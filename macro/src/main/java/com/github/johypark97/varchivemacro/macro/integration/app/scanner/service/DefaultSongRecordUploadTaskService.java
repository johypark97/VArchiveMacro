package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.uploader.SongRecordUploader;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.SongRecordUploaderFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.SongRecordUploadTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.SongRecordUploadTask;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultSongRecordUploadTaskService implements SongRecordUploadTaskService {
    private final ConfigRepository configRepository;
    private final SongRecordRepository songRecordRepository;
    private final SongRepository songRepository;
    private final UpdatedSongRecordRepository updatedSongRecordRepository;

    private final SongRecordUploaderFactory songRecordUploaderFactory;

    public DefaultSongRecordUploadTaskService(ConfigRepository configRepository,
            SongRecordRepository songRecordRepository, SongRepository songRepository,
            UpdatedSongRecordRepository updatedSongRecordRepository,
            SongRecordUploaderFactory songRecordUploaderFactory) {
        this.configRepository = configRepository;
        this.songRecordRepository = songRecordRepository;
        this.songRecordUploaderFactory = songRecordUploaderFactory;
        this.songRepository = songRepository;
        this.updatedSongRecordRepository = updatedSongRecordRepository;
    }

    @Override
    public Task<Map<Integer, SongRecordUploadTaskResult>> createTask(
            List<Integer> selectedEntryIdList) throws Exception {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configRepository.findScannerConfig();

        SongRecordUploader songRecordUploader =
                songRecordUploaderFactory.create(config.accountFile());

        List<UpdatedSongRecordEntry> entryList =
                updatedSongRecordRepository.findAllById(selectedEntryIdList);

        Set<Song> duplicateTitleSongSet = Set.copyOf(songRepository.filterSongByDuplicateTitle());

        return TaskManager.getInstance().register(SongRecordUploadTask.class,
                new SongRecordUploadTask(songRecordRepository, songRepository, songRecordUploader,
                        entryList, duplicateTitleSongSet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(SongRecordUploadTask.class);
    }
}
