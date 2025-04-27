package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import java.nio.file.Path;

public class RemoteSongRecordRepositoryLoader implements SongRecordRepositoryLoader {
    private final Path cachePath;
    private final String djName;

    public RemoteSongRecordRepositoryLoader(Path cachePath, String djName) {
        this.cachePath = cachePath;
        this.djName = djName;
    }

    @Override
    public DefaultRecordManager load() throws Exception {
        return new DefaultRecordManager(djName);
    }

    @Override
    public Path getSavePath() {
        return cachePath;
    }
}
