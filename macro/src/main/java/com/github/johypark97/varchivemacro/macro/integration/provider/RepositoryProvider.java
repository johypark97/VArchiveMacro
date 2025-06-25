package com.github.johypark97.varchivemacro.macro.integration.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.config.infra.repository.DefaultConfigRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.infra.repository.DefaultCaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.infra.repository.DefaultSongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.SongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.repository.DefaultSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.repository.DefaultSongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.service.SongRepositoryLoader;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public enum RepositoryProvider {
    INSTANCE; // Singleton

    private final Path SONG_DATABASE_PATH = Path.of("data/song.db");

    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize() {
        if (initialized.get()) {
            throw new IllegalStateException("RepositoryProvider is already initialized.");
        }

        instanceManager.setConstructor(CaptureRepository.class, DefaultCaptureRepository::new);

        instanceManager.setConstructor(ConfigRepository.class, DefaultConfigRepository::new);

        instanceManager.setConstructor(SongCaptureLinkRepository.class,
                DefaultSongCaptureLinkRepository::new);

        instanceManager.setConstructor(SongRecordRepository.class,
                DefaultSongRecordRepository::new);

        instanceManager.setConstructor(SongRepository.class,
                () -> new DefaultSongRepository(new SongRepositoryLoader(SONG_DATABASE_PATH)));

        initialized.set(true);
    }

    public CaptureRepository getCaptureRepository() {
        return getInstance(CaptureRepository.class);
    }

    public ConfigRepository getConfigRepository() {
        return getInstance(ConfigRepository.class);
    }

    public SongCaptureLinkRepository getSongCaptureLinkRepository() {
        return getInstance(SongCaptureLinkRepository.class);
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
