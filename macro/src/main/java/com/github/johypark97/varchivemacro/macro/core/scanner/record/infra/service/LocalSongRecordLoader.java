package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import java.nio.file.Path;
import java.util.List;

public class LocalSongRecordLoader extends AbstractSongRecordLoader {
    private final Path path;

    public LocalSongRecordLoader(Path path) {
        this.path = path;
    }

    @Override
    public List<SongRecordTable> load() throws Exception {
        return convertRecord(new DefaultRecordManager(path));
    }
}
