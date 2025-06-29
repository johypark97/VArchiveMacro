package com.github.johypark97.varchivemacro.macro.integration.app.scanner.service;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.SongRecordUploadTaskResult;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.task.SongRecordUploadTask;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.Task;

public class DefaultSongRecordUploadTaskService implements SongRecordUploadTaskService {
    private final SongRecordRepository songRecordRepository;
    private final SongRepository songRepository;
    private final UpdatedSongRecordRepository updatedSongRecordRepository;

    private final SongRecordUploadService songRecordUploadService;
    private final SongTitleService songTitleService;

    public DefaultSongRecordUploadTaskService(SongRecordRepository songRecordRepository,
            SongRepository songRepository, UpdatedSongRecordRepository updatedSongRecordRepository,
            SongRecordUploadService songRecordUploadService, SongTitleService songTitleService) {
        this.songRecordRepository = songRecordRepository;
        this.songRepository = songRepository;
        this.updatedSongRecordRepository = updatedSongRecordRepository;

        this.songRecordUploadService = songRecordUploadService;
        this.songTitleService = songTitleService;
    }

    @Override
    public Task<Map<Integer, SongRecordUploadTaskResult>> createTask(
            List<Integer> selectedEntryIdList) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        List<UpdatedSongRecordEntry> entryList =
                updatedSongRecordRepository.findAllById(selectedEntryIdList);

        Set<Song> duplicateTitleSongSet = Set.copyOf(songRepository.filterSongByDuplicateTitle());

        return TaskManager.getInstance().register(SongRecordUploadTask.class,
                new SongRecordUploadTask(songRecordRepository, songRepository,
                        songRecordUploadService, songTitleService, entryList,
                        duplicateTitleSongSet));
    }

    @Override
    public boolean stopTask() {
        return TaskManager.Helper.cancel(SongRecordUploadTask.class);
    }
}
