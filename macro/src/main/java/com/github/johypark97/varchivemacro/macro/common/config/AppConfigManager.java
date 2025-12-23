package com.github.johypark97.varchivemacro.macro.common.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AppConfigManager {
    INSTANCE; // Singleton

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfigManager.class);

    private static final Path CONFIG_FILE_PATH = Path.of("config.json");

    private final AtomicBoolean initialized = new AtomicBoolean();
    private final AtomicReference<Exception> initializationException = new AtomicReference<>();

    private AppConfigService appConfigService;

    public synchronized void initialize() {
        if (initialized.get()) {
            throw new IllegalStateException("AppConfigManager is already initialized.");
        }

        AppConfigRepository appConfigRepository = new DefaultAppConfigRepository(CONFIG_FILE_PATH);
        appConfigService = new DefaultAppConfigService(appConfigRepository);

        try {
            appConfigService.load();
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Failed to load config file.");
            initializationException.set(e);
        }

        initialized.set(true);
    }

    public Optional<Exception> getInitializationException() {
        checkInitialization();

        return Optional.ofNullable(initializationException.get());
    }

    public AppConfigService getAppConfigService() {
        checkInitialization();

        return appConfigService;
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("AppConfigManager is not initialized.");
        }
    }
}
