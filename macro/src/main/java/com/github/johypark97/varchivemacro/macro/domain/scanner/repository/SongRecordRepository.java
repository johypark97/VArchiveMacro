package com.github.johypark97.varchivemacro.macro.domain.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import java.util.List;

public interface SongRecordRepository {
    boolean isEmpty();

    void deleteAll();

    void save(SongRecordTable value);

    void saveAll(List<SongRecordTable> value);

    SongRecordTable findById(int songId);

    List<SongRecordTable> findAll();
}
