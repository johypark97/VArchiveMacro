package com.github.johypark97.varchivemacro.macro.core.scanner.record.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.LocalSongRecordLoadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.LocalSongRecordSaveService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.RemoteSongRecordLoadService;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SongRecordStorageService {
    private final SongRecordRepository songRecordRepository;

    private final Path songRecordFilePath;

    public SongRecordStorageService(SongRecordRepository songRecordRepository,
            Path songRecordFilePath) {
        this.songRecordFilePath = songRecordFilePath;
        this.songRecordRepository = songRecordRepository;
    }

    public void loadFromLocal() throws Exception {
        List<SongRecordTable> list = new LocalSongRecordLoadService(songRecordFilePath).load();

        songRecordRepository.deleteAll();
        songRecordRepository.saveAll(list);
    }

    public void loadFromRemote(String djName) throws Exception {
        List<SongRecordTable> list = new RemoteSongRecordLoadService(djName).load();

        songRecordRepository.deleteAll();
        songRecordRepository.saveAll(list);
    }

    public void saveToLocal() throws IOException {
        new LocalSongRecordSaveService(songRecordFilePath).save(songRecordRepository.findAll());
    }
}
