package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.NewRecordDataRepository;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import java.util.ArrayList;
import java.util.List;

public class DefaultNewRecordDataRepository implements NewRecordDataRepository {
    private final List<NewRecordData> newRecordDataList = new ArrayList<>();

    @Override
    public List<NewRecordData> copyNewRecordDataList() {
        return List.copyOf(newRecordDataList);
    }

    @Override
    public boolean isEmpty() {
        return newRecordDataList.isEmpty();
    }

    @Override
    public void clear() {
        newRecordDataList.clear();
    }

    @Override
    public NewRecordData createNewRecordData(Song song, LocalRecord previousRecord,
            LocalRecord newRecord) {
        int id = newRecordDataList.size();

        NewRecordData data = new NewRecordData(id, song, previousRecord, newRecord);
        newRecordDataList.add(data);

        return data;
    }
}
