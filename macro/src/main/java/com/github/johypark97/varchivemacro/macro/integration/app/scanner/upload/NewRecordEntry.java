package com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;

public record NewRecordEntry(int updatedSongRecordEntryId, Song song, RecordButton button,
                             RecordPattern pattern, SongRecord previousRecord,
                             SongRecord newRecord) {
    public static NewRecordEntry from(UpdatedSongRecordEntry updatedSongRecordEntry, Song song,
            SongRecord previousRecord) {
        return new NewRecordEntry(updatedSongRecordEntry.entryId(), song,
                updatedSongRecordEntry.record().button(), updatedSongRecordEntry.record().pattern(),
                previousRecord, updatedSongRecordEntry.record().newRecord());
    }
}
