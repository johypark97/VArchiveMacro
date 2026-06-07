package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.storage;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.io.IOException;
import java.util.List;

public interface RecordStorage {
    List<SongRecordTable> load() throws IOException;

    void save(List<SongRecordTable> recordTableList) throws IOException;
}
