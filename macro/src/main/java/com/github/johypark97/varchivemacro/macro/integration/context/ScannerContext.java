package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.repository.CaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.infra.repository.DefaultCaptureRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.infra.repository.DiskCaptureImageRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.repository.SongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.infra.repository.DefaultSongCaptureLinkRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.ScannerAnalysisService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.ScannerReviewService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongCaptureLinkingService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.scanner.ScannerScannerService;
import java.io.IOException;
import java.nio.file.Path;

public class ScannerContext implements Context {
    // constants
    private static final Path SONG_TITLE_FILE_PATH = Path.of("data/titles.json");
    private static final Path TRAINEDDATA_DIRECTORY_PATH = Path.of("data");
    private static final String ENG_LANGUAGE = "eng";
    private static final String SONG_TITLE_LANGUAGE = "djmax";

    // repositories
    final CaptureImageRepository captureImageRepository;
    final CaptureRepository captureRepository = new DefaultCaptureRepository();
    final SongCaptureLinkRepository songCaptureLinkRepository =
            new DefaultSongCaptureLinkRepository();

    // services
    public final CaptureImageService captureImageService;
    public final CaptureService captureService = new CaptureService(captureRepository);
    public final OcrServiceFactory commonOcrServiceFactory =
            new OcrServiceFactory(TRAINEDDATA_DIRECTORY_PATH, ENG_LANGUAGE);
    public final OcrServiceFactory songTitleOcrServiceFactory =
            new OcrServiceFactory(TRAINEDDATA_DIRECTORY_PATH, SONG_TITLE_LANGUAGE);
    public final PixImageService pixImageService = new PixImageService();
    public final SongCaptureLinkService songCaptureLinkService =
            new SongCaptureLinkService(songCaptureLinkRepository);
    public final SongTitleService songTitleService = new SongTitleService(SONG_TITLE_FILE_PATH);

    // integrations
    public final SongCaptureLinkingService songCaptureLinkingService;

    // use cases
    public final ScannerAnalysisService scannerAnalysisService;
    public final ScannerReviewService scannerReviewService;
    public final ScannerScannerService scannerScannerService;

    public ScannerContext(GlobalContext globalContext, boolean debug) throws IOException {
        // repositories
        Path cacheDirectoryPath = PathValidator.validateAndConvert(
                globalContext.configService.findScannerConfig().cacheDirectory());

        captureImageRepository = new DiskCaptureImageRepository(cacheDirectoryPath);

        // services
        captureImageService = new CaptureImageService(captureImageRepository);

        // integrations
        songCaptureLinkingService =
                new SongCaptureLinkingService(captureService, songCaptureLinkService,
                        globalContext.songService, songTitleService);

        // use cases
        scannerScannerService =
                new ScannerScannerService(captureImageService, globalContext.captureRegionService,
                        captureService, globalContext.configService, pixImageService,
                        globalContext.songService, songTitleService, songTitleOcrServiceFactory,
                        debug);

        scannerReviewService = new ScannerReviewService(captureImageService, captureService,
                songCaptureLinkService, songCaptureLinkingService, globalContext.songService);

        scannerAnalysisService = new ScannerAnalysisService(captureImageService, captureService,
                globalContext.configService, pixImageService, songCaptureLinkService,
                globalContext.songService, commonOcrServiceFactory);
    }
}
