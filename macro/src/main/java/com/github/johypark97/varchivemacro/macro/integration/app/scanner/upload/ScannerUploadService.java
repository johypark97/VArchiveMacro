package com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.SongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.UpdatedSongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.concurrent.Task;

public class ScannerUploadService {
    private final SongCaptureLinkService songCaptureLinkService;
    private final SongRecordService songRecordService;
    private final SongRecordUploadService songRecordUploadService;
    private final SongService songService;
    private final SongTitleService songTitleService;
    private final UpdatedSongRecordService updatedSongRecordService;

    public ScannerUploadService(SongCaptureLinkService songCaptureLinkService,
            SongRecordService songRecordService, SongRecordUploadService songRecordUploadService,
            SongService songService, SongTitleService songTitleService,
            UpdatedSongRecordService updatedSongRecordService) {
        this.songCaptureLinkService = songCaptureLinkService;
        this.songRecordService = songRecordService;
        this.songRecordUploadService = songRecordUploadService;
        this.songService = songService;
        this.songTitleService = songTitleService;
        this.updatedSongRecordService = updatedSongRecordService;
    }

    public void clear() {
        updatedSongRecordService.deleteAll();
    }

    public void collect(List<Integer> songIdList) {
        if (!updatedSongRecordService.isEmpty()) {
            throw new IllegalStateException();
        }

        Map<Song, Map<CaptureEntry, SongCaptureLink>> linkMap =
                songCaptureLinkService.groupBySong();

        for (Integer songId : songIdList) {
            SongRecordTable songRecordTable = songRecordService.findById(songId);

            Song song = songService.findSongById(songId);
            if (song == null) {
                continue;
            }

            Map<CaptureEntry, SongCaptureLink> linkedCaptureEntryMap = linkMap.get(song);
            if (linkedCaptureEntryMap == null) {
                continue;
            }

            linkedCaptureEntryMap.keySet().stream().findFirst().ifPresent(
                    captureEntry -> captureEntry.capture().songRecordStream().filter(cell -> {
                        SongRecord newRecord = cell.songRecord();
                        SongRecord previousRecord =
                                songRecordTable.getSongRecord(cell.button(), cell.pattern());
                        return newRecord.compareTo(previousRecord) > 0;
                    }).forEach(cell -> updatedSongRecordService.save(
                            new UpdatedSongRecord(songRecordTable.songId, cell.button(),
                                    cell.pattern(), cell.songRecord()))));
        }
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

    public List<NewRecordEntry> getAllNewRecordList() {
        return updatedSongRecordService.findAll().stream().map(updatedSongRecordEntry -> {
            int songId = updatedSongRecordEntry.record().songId();

            Song song = songService.findSongById(songId);
            SongRecordTable songRecordTable = songRecordService.findById(songId);
            SongRecord previousRecord =
                    songRecordTable.getSongRecord(updatedSongRecordEntry.record().button(),
                            updatedSongRecordEntry.record().pattern());

            return NewRecordEntry.from(updatedSongRecordEntry, song, previousRecord);
        }).toList();
    }
}
