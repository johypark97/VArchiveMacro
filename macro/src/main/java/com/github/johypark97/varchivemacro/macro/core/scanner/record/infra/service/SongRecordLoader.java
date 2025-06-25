package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.util.List;

public interface SongRecordLoader {
    List<SongRecordTable> load() throws Exception;
}
