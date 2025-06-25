package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import java.util.List;

public class UpdatedSongRecordCollectingService {
    public List<UpdatedSongRecord> collect(SongRecordTable songRecordTable, Capture capture) {
        return capture.captureAreaStream().filter(cell -> {
            SongRecord newRecord = cell.area().record();
            SongRecord previousRecord =
                    songRecordTable.getSongRecord(cell.button(), cell.pattern());
            return newRecord.compareTo(previousRecord) > 0;
        }).map(cell -> new UpdatedSongRecord(songRecordTable.songId, cell.button(), cell.pattern(),
                cell.area().record())).toList();
    }
}
