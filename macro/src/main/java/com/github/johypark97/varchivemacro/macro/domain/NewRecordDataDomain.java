package com.github.johypark97.varchivemacro.macro.domain;

import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import java.util.List;

public interface NewRecordDataDomain {
    List<NewRecordData> copyNewRecordDataList();

    boolean isEmpty();

    void clear();

    NewRecordData createNewRecordData(Song song, LocalRecord previousRecord, LocalRecord newRecord);
}
