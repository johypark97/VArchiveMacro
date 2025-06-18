package com.github.johypark97.varchivemacro.macro.application.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.application.data.ProgramDataVersionService;
import com.github.johypark97.varchivemacro.macro.application.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.application.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.OcrFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.SongTitleMapperFactory;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.CollectionScanTaskService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.DefaultCollectionScanTaskService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.SongRecordLoadService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.SongRecordSaveService;
import com.github.johypark97.varchivemacro.macro.application.service.WebBrowserService;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleNormalizer;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.HostServices;

public enum ServiceProvider {
    INSTANCE; // Singleton

    private final Path PROGRAM_DATA_DIRECTORY_PATH = Path.of("data");
    private final Path RECORD_FILE_PATH = Path.of("records.json");

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

    public MacroService getMacroService() {
        return new DefaultMacroService(RepositoryProvider.INSTANCE.getConfigRepository());
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
