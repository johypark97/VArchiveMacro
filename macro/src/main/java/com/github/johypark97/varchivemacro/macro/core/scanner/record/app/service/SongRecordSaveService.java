package com.github.johypark97.varchivemacro.macro.core.scanner.record.app.service;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.service.LocalSongRecordSaver;
import java.io.IOException;
import java.nio.file.Path;

public class SongRecordSaveService {
    private final SongRecordRepository songRecordRepository;

    private final Path path;

    public SongRecordSaveService(SongRecordRepository songRecordRepository, Path path) {
        this.songRecordRepository = songRecordRepository;

        this.path = path;
    }

    public void save() throws IOException {
        new LocalSongRecordSaver(path).save(songRecordRepository.findAll());
    }
}
