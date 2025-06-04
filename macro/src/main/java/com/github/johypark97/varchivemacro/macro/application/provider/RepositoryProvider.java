package com.github.johypark97.varchivemacro.macro.application.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.DefaultConfigRepository;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public enum RepositoryProvider {
    INSTANCE; // Singleton

    private final Path CONFIG_FILE_PATH = Path.of("config.json");

    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize() {
        if (initialized.get()) {
            throw new IllegalStateException("RepositoryProvider is already initialized.");
        }

        instanceManager.setConstructor(ConfigRepository.class,
                () -> new DefaultConfigRepository(CONFIG_FILE_PATH));

        initialized.set(true);
    }

    public ConfigRepository getConfigRepository() {
        return getInstance(ConfigRepository.class);
    }

    private <T> T getInstance(Class<T> cls) {
        checkInitialization();

        return instanceManager.getInstance(cls);
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("RepositoryProvider is not initialized.");
        }
    }
}
