package com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.SongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.common.InterruptibleTask;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongRecordUploadTask extends InterruptibleTask<List<SongRecordUploadTaskResult>> {
    private static final Logger LOGGER = LoggerFactory.getLogger("recordUpload");

    private final SongRecordService songRecordService;
    private final SongRecordUploadService songRecordUploadService;
    private final SongService songService;
    private final SongTitleService songTitleService;

    private final List<UpdatedSongRecordEntry> entryList;
    private final Set<Song> duplicateTitleSongSet;

    public SongRecordUploadTask(SongRecordService songRecordService,
            SongRecordUploadService songRecordUploadService, SongService songService,
            SongTitleService songTitleService, List<UpdatedSongRecordEntry> entryList,
            Set<Song> duplicateTitleSongSet) {
        this.songRecordService = songRecordService;
        this.songRecordUploadService = songRecordUploadService;
        this.songService = songService;
        this.songTitleService = songTitleService;

        this.duplicateTitleSongSet = duplicateTitleSongSet;
        this.entryList = entryList;
    }

    @Override
    protected List<SongRecordUploadTaskResult> callTask() throws Exception {
        updateProgress(0, 1);

        Map<Integer, SongRecordUploadTaskResult> resultMap = entryList.stream().collect(
                Collectors.toMap(UpdatedSongRecordEntry::entryId,
                        x -> new SongRecordUploadTaskResult(x.entryId())));

        int count = 0;
        int total = resultMap.size();

        Exception exception = null;
        try {
            for (UpdatedSongRecordEntry entry : entryList) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                Song song = songService.findSongById(entry.record().songId());

                String title = songTitleService.getRemoteTitleOrDefault(song);
                String composer = duplicateTitleSongSet.contains(song) ? song.composer() : null;
                RecordButton button = entry.record().button();
                RecordPattern pattern = entry.record().pattern();
                SongRecord newRecord = entry.record().newRecord();

                boolean updated =
                        songRecordUploadService.upload(title, composer, button, pattern, newRecord);

                LOGGER.atInfo()
                        .log("[{}] {} - {} {} {} {} ({})", song.pack().name(), song.composer(),
                                song.title(), button, pattern, newRecord.rate(),
                                newRecord.maxCombo() ? "MaxCombo" : "");

                SongRecordTable table = songRecordService.findById(song.songId());
                table.setSongRecord(button, pattern, newRecord);

                resultMap.get(entry.entryId()).setStatus(updated
                        ? SongRecordUploadTaskResult.Status.UPDATED
                        : SongRecordUploadTaskResult.Status.HIGHER_RECORD_EXISTS);

                updateProgress(++count, total);
            }
        } catch (Exception e) {
            exception = e;
        }

        List<SongRecordUploadTaskResult> resultList = resultMap.values().stream().toList();

        if (exception != null) {
            updateValue(resultList);
            throw exception;
        }

        return resultList;
    }
}
