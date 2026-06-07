package com.github.johypark97.varchivemacro.macro.core.scanner.record.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.storage.RecordStorage;
import java.io.IOException;
import java.util.List;

public class SongRecordStorageService {
    private final SongRecordRepository songRecordRepository;
    private final RecordStorage recordStorage;

    public SongRecordStorageService(
            SongRecordRepository songRecordRepository,
            RecordStorage recordStorage
    ) {
        this.recordStorage = recordStorage;
        this.songRecordRepository = songRecordRepository;
    }

    public void loadFromLocal() throws IOException {
        List<SongRecordTable> list = recordStorage.load();

        songRecordRepository.deleteAll();
        songRecordRepository.saveAll(list);
    }

    public void saveToLocal() throws IOException {
        recordStorage.save(songRecordRepository.findAll());
    }
}
