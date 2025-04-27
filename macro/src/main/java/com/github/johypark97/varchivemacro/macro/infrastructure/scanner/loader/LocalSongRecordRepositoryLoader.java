package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import java.nio.file.Path;

public class LocalSongRecordRepositoryLoader implements SongRecordRepositoryLoader {
    private final Path path;

    public LocalSongRecordRepositoryLoader(Path path) {
        this.path = path;
    }

    @Override
    public DefaultRecordManager load() throws Exception {
        return new DefaultRecordManager(path);
    }

    @Override
    public Path getSavePath() {
        return path;
    }
}
