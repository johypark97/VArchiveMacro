package com.github.johypark97.varchivemacro.lib.jfx;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javafx.concurrent.Task;

public class TaskManager {
    private final Map<Class<? extends Task<?>>, Reference<Task<?>>> taskMap = new HashMap<>();

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        return Singleton.INSTANCE;
    }

    public long countTask() {
        lock.readLock().lock();
        try {
            return taskMap.values().stream().filter(x -> !x.refersTo(null)).count();
        } finally {
            lock.readLock().unlock();
        }
    }

    public long countRunning() {
        lock.readLock().lock();
        try {
            return taskMap.values().stream().map(Reference::get).filter(Objects::nonNull)
                    .filter(Task::isRunning).count();
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isRunningAny() {
        return countRunning() > 0;
    }

    public <T extends Task<?>> T get(Class<T> cls) {
        lock.readLock().lock();
        try {
            return get_lockless(cls);
        } finally {
            lock.readLock().unlock();
        }
    }

    public <T extends Task<?>> T register(Class<T> cls, T task) {
        lock.writeLock().lock();
        try {
            T x = get_lockless(cls);

            if (x != null && x.isRunning()) {
                return null;
            }

            taskMap.put(cls, new WeakReference<>(task));

            return task;
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected <T extends Task<?>> T get_lockless(Class<T> cls) {
        @SuppressWarnings("unchecked")
        Reference<T> reference = (Reference<T>) taskMap.get(cls);

        return reference == null ? null : reference.get();
    }

    public static class Helper {
        public static boolean cancel(Class<? extends Task<?>> cls) {
            Task<?> task = getInstance().get(cls);

            return task != null && task.cancel();
        }
    }


    private static class Singleton {
        private static final TaskManager INSTANCE = new TaskManager();
    }
}
