package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import java.util.List;

public interface SongRecordLoader {
    List<SongRecordTable> load() throws Exception;
}
