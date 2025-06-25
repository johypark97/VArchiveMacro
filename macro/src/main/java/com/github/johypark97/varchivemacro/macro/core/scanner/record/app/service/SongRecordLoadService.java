package com.github.johypark97.varchivemacro.macro.core.scanner.record.app.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.LocalSongRecordLoader;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.RemoteSongRecordLoader;
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
