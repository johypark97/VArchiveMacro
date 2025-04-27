package com.github.johypark97.varchivemacro.macro.domain.scanner.service;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Capture;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.UpdatedSongRecord;
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
