package com.github.johypark97.varchivemacro.lib.jfx;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import javafx.concurrent.Service;

public class ServiceManager {
    private final Map<Class<? extends Service<?>>, Service<?>> serviceMap = new HashMap<>();

    private ServiceManager() {
    }

    public static ServiceManager getInstance() {
        return Singleton.INSTANCE;
    }

    public boolean isRunningAny() {
        return countRunning() > 0;
    }

    public long countRunning() {
        return serviceMap.values().stream().filter(Service::isRunning).count();
    }

    public long countService() {
        return serviceMap.size();
    }

    public <T extends Service<?>> T create(Class<T> cls) {
        return create(cls, () -> {
            try {
                return cls.getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T extends Service<?>> T create(Class<T> cls, Supplier<T> constructor) {
        if (serviceMap.containsKey(cls)) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T service = (T) serviceMap.computeIfAbsent(cls, x -> constructor.get());

        return service;
    }

    public <T extends Service<?>> T get(Class<T> cls) {
        @SuppressWarnings("unchecked")
        T service = (T) serviceMap.get(cls);

        return service;
    }

    public <T extends Service<?>> T remove(Class<T> cls) {
        @SuppressWarnings("unchecked")
        T service = (T) serviceMap.remove(cls);

        return service;
    }

    private static class Singleton {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }
}
