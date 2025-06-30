package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.infra.repository.DefaultCaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.infra.repository.DiskCaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.infra.repository.DefaultSongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.service.CollectionScanTaskService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.service.DefaultCollectionScanTaskService;
import java.io.IOException;
import java.nio.file.Path;

public class ScannerContext implements Context {
    // constants
    private static final Path SONG_TITLE_FILE_PATH = Path.of("data/titles.json");
    private static final Path TRAINEDDATA_DIRECTORY_PATH = Path.of("data");
    private static final String SONG_TITLE_LANGUAGE = "djmax";

    // repositories
    final CaptureImageRepository captureImageRepository;
    final CaptureRepository captureRepository = new DefaultCaptureRepository();
    final SongCaptureLinkRepository songCaptureLinkRepository =
            new DefaultSongCaptureLinkRepository();

    // services
    public final CaptureImageService captureImageService;
    public final CaptureService captureService = new CaptureService(captureRepository);
    public final OcrServiceFactory songTitleOcrServiceFactory =
            new OcrServiceFactory(TRAINEDDATA_DIRECTORY_PATH, SONG_TITLE_LANGUAGE);
    public final SongTitleService songTitleService = new SongTitleService(SONG_TITLE_FILE_PATH);

    // integrations
    public final CollectionScanTaskService collectionScanTaskService;

    public ScannerContext(GlobalContext globalContext) throws IOException {
        Path cacheDirectoryPath = PathValidator.validateAndConvert(
                globalContext.configService.findScannerConfig().cacheDirectory());

        captureImageRepository = new DiskCaptureImageRepository(cacheDirectoryPath);
        captureImageService = new CaptureImageService(captureImageRepository);

        collectionScanTaskService =
                new DefaultCollectionScanTaskService(captureImageService, captureService,
                        globalContext.configService, songCaptureLinkRepository,
                        globalContext.songService, songTitleService, songTitleOcrServiceFactory);
    }
}
