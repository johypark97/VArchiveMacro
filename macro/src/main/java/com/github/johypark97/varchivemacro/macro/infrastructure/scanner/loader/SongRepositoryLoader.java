package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.lib.scanner.database.CachedReadOnlySongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import java.nio.file.Path;
import java.sql.SQLException;

public class SongRepositoryLoader {
    private final Path path;

    public SongRepositoryLoader(Path path) {
        this.path = path;
    }

    public SongDatabase load() throws SQLException {
        return new CachedReadOnlySongDatabase(path);
    }
}
