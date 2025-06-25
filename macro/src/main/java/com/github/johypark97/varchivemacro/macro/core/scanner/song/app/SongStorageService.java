package com.github.johypark97.varchivemacro.macro.core.scanner.song.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.repository.DefaultSongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.service.DefaultSongRepositoryLoadService;
import java.nio.file.Path;
import java.sql.SQLException;

public class SongStorageService {
    private final SongRepository songRepository;
    private final Path songDatabaseFilePath;

    public SongStorageService(SongRepository songRepository, Path songDatabaseFilePath) {
        this.songDatabaseFilePath = songDatabaseFilePath;
        this.songRepository = songRepository;
    }

    public void load() throws SQLException {
        if (songRepository instanceof DefaultSongRepository repository) {
            new DefaultSongRepositoryLoadService(repository).load(songDatabaseFilePath);
        } else {
            throw new UnsupportedOperationException(songRepository.getClass().getName());
        }
    }
}
