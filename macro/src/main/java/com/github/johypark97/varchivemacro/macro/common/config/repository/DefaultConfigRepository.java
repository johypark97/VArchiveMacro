package com.github.johypark97.varchivemacro.macro.common.config.repository;

import com.github.johypark97.varchivemacro.macro.common.config.storage.ConfigStorage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public class DefaultConfigRepository<T> implements ConfigRepository<T> {
    private final AtomicReference<T> configCache = new AtomicReference<>();

    private final ConfigStorage<T> configStorage;

    public DefaultConfigRepository(ConfigStorage<T> configStorage) {
        this.configStorage = configStorage;
    }

    @Override
    public T find() {
        return configCache.get();
    }

    @Override
    public void save(T config) {
        configCache.set(Objects.requireNonNull(config));
    }

    @Override
    public void update(UnaryOperator<T> updateFunction) {
        configCache.updateAndGet(updateFunction);
    }

    @Override
    public void refresh() throws IOException {
        configCache.set(configStorage.read());
    }

    @Override
    public void flush() throws IOException {
        T config = configCache.get();

        if (config != null) {
            configStorage.write(config);
        }
    }
}
