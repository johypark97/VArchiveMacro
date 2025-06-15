package com.github.johypark97.varchivemacro.macro.application.scanner.task;

import com.github.johypark97.varchivemacro.macro.application.common.InterruptibleTask;
import com.github.johypark97.varchivemacro.macro.application.scanner.model.SongRecordUploadTaskResult;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.uploader.SongRecordUploader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SongRecordUploadTask
        extends InterruptibleTask<Map<Integer, SongRecordUploadTaskResult>> {
    private final SongRecordRepository songRecordRepository;
    private final SongRepository songRepository;

    private final SongRecordUploader songRecordUploader;

    private final List<UpdatedSongRecordEntry> entryList;
    private final Set<Song> duplicateTitleSongSet;

    public SongRecordUploadTask(SongRecordRepository songRecordRepository,
            SongRepository songRepository, SongRecordUploader songRecordUploader,
            List<UpdatedSongRecordEntry> entryList, Set<Song> duplicateTitleSongSet) {
        this.duplicateTitleSongSet = duplicateTitleSongSet;
        this.entryList = entryList;
        this.songRecordRepository = songRecordRepository;
        this.songRecordUploader = songRecordUploader;
        this.songRepository = songRepository;
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

            RecordButton button = entry.record().button();
            RecordPattern pattern = entry.record().pattern();
            SongRecord newRecord = entry.record().newRecord();
            boolean includeComposer = duplicateTitleSongSet.contains(song);

            boolean updated =
                    songRecordUploader.upload(song, button, pattern, newRecord, includeComposer);

            SongRecordTable table = songRecordRepository.findById(song.songId());
            table.setSongRecord(button, pattern, newRecord);

            resultMap.get(entry.entryId()).setStatus(updated
                    ? SongRecordUploadTaskResult.Status.UPDATED
                    : SongRecordUploadTaskResult.Status.HIGHER_RECORD_EXISTS);
        }

        return resultMap;
    }
}
