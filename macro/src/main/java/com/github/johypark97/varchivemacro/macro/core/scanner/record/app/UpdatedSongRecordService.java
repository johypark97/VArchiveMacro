package com.github.johypark97.varchivemacro.macro.core.scanner.record.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import java.util.List;

public class UpdatedSongRecordService implements UpdatedSongRecordRepository {
    private final UpdatedSongRecordRepository updatedSongRecordRepository;

    public UpdatedSongRecordService(UpdatedSongRecordRepository updatedSongRecordRepository) {
        this.updatedSongRecordRepository = updatedSongRecordRepository;
    }

    @Override
    public boolean isEmpty() {
        return updatedSongRecordRepository.isEmpty();
    }

    @Override
    public void deleteAll() {
        updatedSongRecordRepository.deleteAll();
    }

    @Override
    public UpdatedSongRecordEntry save(UpdatedSongRecord value) {
        return updatedSongRecordRepository.save(value);
    }

    @Override
    public List<UpdatedSongRecordEntry> findAll() {
        return updatedSongRecordRepository.findAll();
    }

    @Override
    public List<UpdatedSongRecordEntry> findAllById(Iterable<Integer> iterable) {
        return updatedSongRecordRepository.findAllById(iterable);
    }
}
