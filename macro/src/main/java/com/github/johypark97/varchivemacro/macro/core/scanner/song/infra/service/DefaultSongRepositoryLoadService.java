package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.database.CachedReadOnlySongDatabase;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.repository.DefaultSongRepository;
import java.nio.file.Path;
import java.sql.SQLException;

public class DefaultSongRepositoryLoadService {
    private final DefaultSongRepository defaultSongRepository;

    public DefaultSongRepositoryLoadService(DefaultSongRepository defaultSongRepository) {
        this.defaultSongRepository = defaultSongRepository;
    }

    public void load(Path songDatabaseFilePath) throws SQLException {
        defaultSongRepository.setSongDatabase(new CachedReadOnlySongDatabase(songDatabaseFilePath));
    }
}
