package com.github.johypark97.varchivemacro.macro.domain.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;

public interface SongRecordRepository {
    void load();

    void flush();

    void save(SongRecordTable value);

    SongRecordTable findById(int songId);
}
