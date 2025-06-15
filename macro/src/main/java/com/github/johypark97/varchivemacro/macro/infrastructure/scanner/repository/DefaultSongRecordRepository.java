package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongRecordTable;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSongRecordRepository implements SongRecordRepository {
    private final Map<Integer, SongRecordTable> recordTableMap = new ConcurrentHashMap<>();

    @Override
    public boolean isEmpty() {
        return recordTableMap.isEmpty();
    }

    @Override
    public void deleteAll() {
        recordTableMap.clear();
    }

    @Override
    public void save(SongRecordTable value) {
        recordTableMap.put(value.songId, value);
    }

    @Override
    public void saveAll(List<SongRecordTable> value) {
        value.forEach(this::save);
    }

    @Override
    public SongRecordTable findById(int songId) {
        return recordTableMap.get(songId);
    }

    @Override
    public List<SongRecordTable> findAll() {
        return recordTableMap.values().stream().toList();
    }
}
