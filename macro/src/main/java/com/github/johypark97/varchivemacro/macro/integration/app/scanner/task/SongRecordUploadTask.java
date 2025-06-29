package com.github.johypark97.varchivemacro.macro.integration.app.scanner.task;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.model.SongRecordUploadTaskResult;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SongRecordUploadTask
        extends InterruptibleTask<Map<Integer, SongRecordUploadTaskResult>> {
    private final SongRecordRepository songRecordRepository;
    private final SongRepository songRepository;

    private final SongRecordUploadService songRecordUploadService;
    private final SongTitleService songTitleService;

    private final List<UpdatedSongRecordEntry> entryList;
    private final Set<Song> duplicateTitleSongSet;

    public SongRecordUploadTask(SongRecordRepository songRecordRepository,
            SongRepository songRepository, SongRecordUploadService songRecordUploadService,
            SongTitleService songTitleService, List<UpdatedSongRecordEntry> entryList,
            Set<Song> duplicateTitleSongSet) {
        this.songRecordRepository = songRecordRepository;
        this.songRepository = songRepository;

        this.songRecordUploadService = songRecordUploadService;
        this.songTitleService = songTitleService;

        this.duplicateTitleSongSet = duplicateTitleSongSet;
        this.entryList = entryList;
    }

    @Override
    protected Map<Integer, SongRecordUploadTaskResult> callTask() throws Exception {
        Map<Integer, SongRecordUploadTaskResult> resultMap = entryList.stream().collect(
                Collectors.toMap(UpdatedSongRecordEntry::entryId,
                        x -> new SongRecordUploadTaskResult(x.entryId())));

        for (UpdatedSongRecordEntry entry : entryList) {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }

            Song song = songRepository.findSongById(entry.record().songId());

            String title = songTitleService.getRemoteTitleOrDefault(song);
            String composer = duplicateTitleSongSet.contains(song) ? song.composer() : null;
            RecordButton button = entry.record().button();
            RecordPattern pattern = entry.record().pattern();
            SongRecord newRecord = entry.record().newRecord();

            boolean updated =
                    songRecordUploadService.upload(title, composer, button, pattern, newRecord);

            SongRecordTable table = songRecordRepository.findById(song.songId());
            table.setSongRecord(button, pattern, newRecord);

            resultMap.get(entry.entryId()).setStatus(updated
                    ? SongRecordUploadTaskResult.Status.UPDATED
                    : SongRecordUploadTaskResult.Status.HIGHER_RECORD_EXISTS);
        }

        return resultMap;
    }
}
