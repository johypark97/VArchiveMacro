package com.github.johypark97.varchivemacro.macro.core.scanner.capture.infra.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCaptureRepository implements CaptureRepository {
    private final Map<Integer, CaptureEntry> entryMap = new HashMap<>();

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
    public CaptureEntry save(Capture value) {
        lock.writeLock().lock();
        try {
            CaptureEntry entry = new CaptureEntry(entryMap.size(), value);
            entryMap.put(entry.entryId(), entry);

            return entry;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<CaptureEntry> findAll() {
        lock.readLock().lock();
        try {
            return entryMap.values().stream().sorted(Comparator.comparingInt(CaptureEntry::entryId))
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public CaptureEntry findById(int entryId) {
        lock.readLock().lock();
        try {
            return entryMap.get(entryId);
        } finally {
            lock.readLock().unlock();
        }
    }
}
