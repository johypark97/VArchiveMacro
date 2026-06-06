package com.github.johypark97.varchivemacro.macro.core.scanner.song.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.storage.SongStorage;
import java.sql.SQLException;

public class SongStorageService {
    private final SongRepository songRepository;
    private final SongStorage songStorage;

    public SongStorageService(SongRepository songRepository, SongStorage songStorage) {
        this.songRepository = songRepository;
        this.songStorage = songStorage;
    }

    public void load() throws SQLException {
        songRepository.deleteAll();
        songRepository.saveAll(songStorage.load());
    }
}
