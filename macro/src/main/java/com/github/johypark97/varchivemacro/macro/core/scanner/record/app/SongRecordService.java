package com.github.johypark97.varchivemacro.macro.core.scanner.record.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import java.util.List;

public class SongRecordService implements SongRecordRepository {
    private final SongRecordRepository songRecordRepository;

    public SongRecordService(SongRecordRepository songRecordRepository) {
        this.songRecordRepository = songRecordRepository;
    }

    @Override
    public boolean isEmpty() {
        return songRecordRepository.isEmpty();
    }

    @Override
    public void deleteAll() {
        songRecordRepository.deleteAll();
    }

    @Override
    public void save(SongRecordTable value) {
        songRecordRepository.save(value);
    }

    @Override
    public void saveAll(List<SongRecordTable> value) {
        songRecordRepository.saveAll(value);
    }

    @Override
    public SongRecordTable findById(int songId) {
        return songRecordRepository.findById(songId);
    }

    @Override
    public List<SongRecordTable> findAll() {
        return songRecordRepository.findAll();
    }
}
