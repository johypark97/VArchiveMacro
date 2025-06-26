package com.github.johypark97.varchivemacro.lib.common.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LazyInstanceManager<T> implements InstanceManager<T> {
    protected final Map<Class<? extends T>, Supplier<? extends T>> constructorMap = new HashMap<>();
    protected final Map<Class<? extends T>, T> instanceMap = new ConcurrentHashMap<>();

    protected <U extends T> U newInstance(Class<U> cls) {
        @SuppressWarnings("unchecked")
        Supplier<U> constructor = (Supplier<U>) constructorMap.get(cls);

        if (constructor == null) {
            throw new NullPointerException("Constructor is null: " + cls);
        }

        return constructor.get();
    }

    @Override
    public <U extends T> void setConstructor(Class<U> cls, Supplier<U> constructor) {
        constructorMap.put(cls, constructor);
    }

    @Override
    public <U extends T> U getInstance(Class<U> cls) { // NOPMD
        @SuppressWarnings("unchecked")
        U instance = (U) instanceMap.computeIfAbsent(cls, this::newInstance);

        return instance;
    }

    @Override
    public <U extends T> U setInstance(Class<U> cls, U instance) {
        @SuppressWarnings("unchecked")
        U previousInstance = (U) instanceMap.put(cls, instance);

        return previousInstance;
    }

    @Override
    public <U extends T> U removeInstance(Class<U> cls) {
        @SuppressWarnings("unchecked")
        U removedInstance = (U) instanceMap.remove(cls);

        return removedInstance;
    }
}
