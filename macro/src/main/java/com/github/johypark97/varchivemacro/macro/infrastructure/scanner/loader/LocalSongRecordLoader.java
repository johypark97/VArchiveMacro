package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
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
