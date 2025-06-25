package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecord;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.UpdatedSongRecordEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultUpdatedSongRecordRepository implements UpdatedSongRecordRepository {
    private final Map<Integer, UpdatedSongRecordEntry> entryMap = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean isEmpty() {
        lock.readLock().lock();
        try {
            return entryMap.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void deleteAll() {
        lock.writeLock().lock();
        try {
            entryMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public UpdatedSongRecordEntry save(UpdatedSongRecord value) {
        lock.writeLock().lock();
        try {
            UpdatedSongRecordEntry entry = new UpdatedSongRecordEntry(entryMap.size(), value);
            entryMap.put(entry.entryId(), entry);

            return entry;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<UpdatedSongRecordEntry> findAll() {
        lock.readLock().lock();
        try {
            return entryMap.values().stream().toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<UpdatedSongRecordEntry> findAllById(Iterable<Integer> iterable) {
        List<UpdatedSongRecordEntry> list = new ArrayList<>();

        lock.readLock().lock();
        try {
            iterable.forEach(x -> list.add(entryMap.get(x)));
        } finally {
            lock.readLock().unlock();
        }

        return list;
    }
}
