package com.github.johypark97.varchivemacro.macro.core.scanner.record.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.service.UpdatedSongRecordCollectingService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.repository.DefaultUpdatedSongRecordRepository;
import java.util.List;

public class UpdatedSongRecordService implements UpdatedSongRecordRepository {
    private final UpdatedSongRecordRepository updatedSongRecordRepository =
            new DefaultUpdatedSongRecordRepository();

    private final UpdatedSongRecordCollectingService updatedSongRecordCollectingService =
            new UpdatedSongRecordCollectingService();

    public List<UpdatedSongRecord> collectUpdatedSongRecord(SongRecordTable songRecordTable,
            Capture capture) {
        return updatedSongRecordCollectingService.collect(songRecordTable, capture);
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
