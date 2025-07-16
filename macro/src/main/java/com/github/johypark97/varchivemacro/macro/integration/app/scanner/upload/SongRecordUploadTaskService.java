package com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.SongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.UpdatedSongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.Task;

public class SongRecordUploadTaskService {
    private final SongRecordService songRecordService;
    private final SongRecordUploadService songRecordUploadService;
    private final SongService songService;
    private final SongTitleService songTitleService;
    private final UpdatedSongRecordService updatedSongRecordService;

    public SongRecordUploadTaskService(SongRecordService songRecordService,
            SongRecordUploadService songRecordUploadService, SongService songService,
            SongTitleService songTitleService, UpdatedSongRecordService updatedSongRecordService) {
        this.songRecordService = songRecordService;
        this.songRecordUploadService = songRecordUploadService;
        this.songService = songService;
        this.songTitleService = songTitleService;
        this.updatedSongRecordService = updatedSongRecordService;
    }

    public Task<Map<Integer, SongRecordUploadTaskResult>> createTask(
            List<Integer> selectedEntryIdList) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        List<UpdatedSongRecordEntry> entryList =
                updatedSongRecordService.findAllById(selectedEntryIdList);

        Set<Song> duplicateTitleSongSet = Set.copyOf(songService.filterSongByDuplicateTitle());

        return TaskManager.getInstance().register(SongRecordUploadTask.class,
                new SongRecordUploadTask(songRecordService, songRecordUploadService, songService,
                        songTitleService, entryList, duplicateTitleSongSet));
    }

    public boolean stopTask() {
        return TaskManager.Helper.cancel(SongRecordUploadTask.class);
    }
}
