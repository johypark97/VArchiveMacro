package com.github.johypark97.varchivemacro.macro.integration.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigStorageService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseService;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseStorageService;
import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.ProgramDataVersionService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.service.SongRecordLoadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.service.SongRecordSaveService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongStorageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleNormalizer;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory.SongTitleMapperFactory;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.service.CollectionScanTaskService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.service.DefaultCollectionScanTaskService;
import com.github.johypark97.varchivemacro.macro.integration.app.service.WebBrowserService;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.HostServices;

public enum ServiceProvider {
    INSTANCE; // Singleton

    private final Path CONFIG_FILE_PATH = Path.of("config.json");
    private final Path PROGRAM_DATA_DIRECTORY_PATH = Path.of("data");
    private final Path RECORD_FILE_PATH = Path.of("records.json");
    private final Path SONG_DATABASE_FILE_PATH = Path.of("data/song.db");

    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize(HostServices hostServices) {
        if (initialized.get()) {
            throw new IllegalStateException("ServiceProvider is already initialized.");
        }

        instanceManager.setConstructor(WebBrowserService.class,
                () -> new WebBrowserService(hostServices));

        initialized.set(true);
    }

    public ConfigService getConfigService() {
        return new ConfigService(RepositoryProvider.INSTANCE.getConfigRepository());
    }

    public ConfigStorageService getConfigStorageService() {
        return new ConfigStorageService(RepositoryProvider.INSTANCE.getConfigRepository(),
                CONFIG_FILE_PATH);
    }

    public MacroService getMacroService() {
        return new DefaultMacroService(RepositoryProvider.INSTANCE.getConfigRepository());
    }

    public OpenSourceLicenseService getOpenSourceLicenseService(
            OpenSourceLicenseRepository openSourceLicenseRepository) {
        return new OpenSourceLicenseService(openSourceLicenseRepository);
    }

    public OpenSourceLicenseStorageService getOpenSourceLicenseStorageService(
            OpenSourceLicenseRepository openSourceLicenseRepository) {
        return new OpenSourceLicenseStorageService(openSourceLicenseRepository);
    }

    public ProgramDataVersionService getProgramDataVersionService() {
        return new ProgramDataVersionService(PROGRAM_DATA_DIRECTORY_PATH);
    }

    public SongRecordLoadService getSongRecordLoadService() {
        return new SongRecordLoadService(RepositoryProvider.INSTANCE.getSongRecordRepository(),
                RECORD_FILE_PATH);
    }

    public SongRecordSaveService getSongRecordSaveService() {
        return new SongRecordSaveService(RepositoryProvider.INSTANCE.getSongRecordRepository(),
                RECORD_FILE_PATH);
    }

    public SongService getSongService() {
        return new SongService(RepositoryProvider.INSTANCE.getSongRepository());
    }

    public SongStorageService getSongStorageService() {
        return new SongStorageService(RepositoryProvider.INSTANCE.getSongRepository(),
                SONG_DATABASE_FILE_PATH);
    }

    public CollectionScanTaskService getCollectionScanTaskService() {
        CaptureRepository captureRepository = RepositoryProvider.INSTANCE.getCaptureRepository();
        ConfigRepository configRepository = RepositoryProvider.INSTANCE.getConfigRepository();
        SongCaptureLinkRepository songCaptureLinkRepository =
                RepositoryProvider.INSTANCE.getSongCaptureLinkRepository();
        SongRepository songRepository = RepositoryProvider.INSTANCE.getSongRepository();

        CaptureImageCacheFactory captureImageCacheFactory =
                FactoryProvider.createCaptureImageCacheFactory();
        OcrFactory songTitleOcrFactory = FactoryProvider.createSongTitleOcrFactory();
        SongTitleMapperFactory songTitleMapperFactory =
                FactoryProvider.createSongTitleMapperFactory();

        SongTitleNormalizer songTitleNormalizer = new SongTitleNormalizer();

        return new DefaultCollectionScanTaskService(captureRepository, configRepository,
                songCaptureLinkRepository, songRepository, captureImageCacheFactory,
                songTitleOcrFactory, songTitleMapperFactory, songTitleNormalizer);
    }

    public WebBrowserService getWebBrowserService() {
        return getInstance(WebBrowserService.class);
    }

    private <T> T getInstance(Class<T> cls) {
        checkInitialization();

        return instanceManager.getInstance(cls);
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("ServiceProvider is not initialized.");
        }
    }
}
