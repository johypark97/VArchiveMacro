package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.converter.RecordManagerConverter;
import java.nio.file.Path;
import java.util.List;

public class LocalSongRecordLoadService implements SongRecordLoadService {
    private final Path path;

    public LocalSongRecordLoadService(Path path) {
        this.path = path;
    }

    @Override
    public List<SongRecordTable> load() throws Exception {
        return RecordManagerConverter.toSongRecordTableList(new DefaultRecordManager(path));
    }
}
