package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader.LocalSongRecordLoader;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader.RemoteSongRecordLoader;
import java.nio.file.Path;
import java.util.List;

public class SongRecordLoadService {
    private final SongRecordRepository songRecordRepository;

    private final Path path;

    public SongRecordLoadService(SongRecordRepository songRecordRepository, Path path) {
        this.songRecordRepository = songRecordRepository;

        this.path = path;
    }

    public void loadFromLocal() throws Exception {
        List<SongRecordTable> list = new LocalSongRecordLoader(path).load();

        songRecordRepository.deleteAll();
        songRecordRepository.saveAll(list);
    }

    public void loadFromRemote(String djName) throws Exception {
        List<SongRecordTable> list = new RemoteSongRecordLoader(djName).load();

        songRecordRepository.deleteAll();
        songRecordRepository.saveAll(list);
    }
}
