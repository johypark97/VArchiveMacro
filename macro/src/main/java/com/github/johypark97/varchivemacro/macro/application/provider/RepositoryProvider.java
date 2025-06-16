package com.github.johypark97.varchivemacro.macro.application.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader.SongRepositoryLoader;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository.DefaultSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository.DefaultSongRepository;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public enum RepositoryProvider {
    INSTANCE; // Singleton

    private final Path CONFIG_FILE_PATH = Path.of("config.json");
    private final Path SONG_DATABASE_PATH = Path.of("data/song.db");

    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize() {
        if (initialized.get()) {
            throw new IllegalStateException("RepositoryProvider is already initialized.");
        }

        instanceManager.setConstructor(ConfigRepository.class,
                () -> new DefaultConfigRepository(CONFIG_FILE_PATH));

        instanceManager.setConstructor(SongRecordRepository.class,
                DefaultSongRecordRepository::new);

        instanceManager.setConstructor(SongRepository.class,
                () -> new DefaultSongRepository(new SongRepositoryLoader(SONG_DATABASE_PATH)));

        initialized.set(true);
    }

    public ConfigRepository getConfigRepository() {
        return getInstance(ConfigRepository.class);
    }

    public SongRecordRepository getSongRecordRepository() {
        return getInstance(SongRecordRepository.class);
    }

    public SongRepository getSongRepository() {
        return getInstance(SongRepository.class);
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
