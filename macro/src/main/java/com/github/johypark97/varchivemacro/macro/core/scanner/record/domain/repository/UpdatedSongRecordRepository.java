package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import java.util.List;

public interface UpdatedSongRecordRepository {
    boolean isEmpty();

    void deleteAll();

    UpdatedSongRecordEntry save(UpdatedSongRecord value);

    List<UpdatedSongRecordEntry> findAll();

    List<UpdatedSongRecordEntry> findAllById(Iterable<Integer> iterable);
}
