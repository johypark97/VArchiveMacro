package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.saver;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import java.io.IOException;
import java.util.List;

public interface SongRecordSaver {
    void save(List<SongRecordTable> value) throws IOException;
}
