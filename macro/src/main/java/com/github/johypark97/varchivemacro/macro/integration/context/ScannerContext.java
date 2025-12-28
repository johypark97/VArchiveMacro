package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.config.AppConfigManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.app.SongRecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
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
import com.github.johypark97.varchivemacro.macro.core.scanner.record.app.UpdatedSongRecordService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.repository.UpdatedSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.repository.DefaultUpdatedSongRecordRepository;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.app.SongTitleService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis.ScannerAnalysisService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.ScannerReviewService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongCaptureLinkingService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.scanner.ScannerScannerService;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.upload.ScannerUploadService;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Set;

public class ScannerContext implements Context {
    // constants
    private static final Path SONG_TITLE_FILE_PATH = Path.of("data/titles.json");
    private static final Path TRAINEDDATA_DIRECTORY_PATH = Path.of("data");
    private static final String ENG_LANGUAGE = "eng";
    private static final String SONG_TITLE_LANGUAGE = "djmax";

    // states
    private final Set<String> selectedCategorySet;

    // repositories
    final CaptureImageRepository captureImageRepository;
    final CaptureRepository captureRepository = new DefaultCaptureRepository();
    final SongCaptureLinkRepository songCaptureLinkRepository =
            new DefaultSongCaptureLinkRepository();
    final UpdatedSongRecordRepository updatedSongRecordRepository =
            new DefaultUpdatedSongRecordRepository();

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
    public final SongRecordUploadService songRecordUploadService;
    public final SongTitleService songTitleService = new SongTitleService(SONG_TITLE_FILE_PATH);
    public final UpdatedSongRecordService updatedSongRecordService =
            new UpdatedSongRecordService(updatedSongRecordRepository);

    // integrations
    public final SongCaptureLinkingService songCaptureLinkingService;

    // use cases
    public final ScannerAnalysisService scannerAnalysisService;
    public final ScannerReviewService scannerReviewService;
    public final ScannerScannerService scannerScannerService;
    public final ScannerUploadService scannerUploadService;

    public ScannerContext(GlobalContext globalContext, Set<String> selectedCategorySet,
            boolean debug)
            throws IOException, GeneralSecurityException, InvalidAccountFileException {
        this.selectedCategorySet = selectedCategorySet;

        AppConfigService appConfigService = AppConfigManager.INSTANCE.getAppConfigService();

        // repositories
        Path cacheDirectoryPath = PathValidator.validateAndConvert(
                appConfigService.getConfig().scannerConfig().cacheDirectory().value());

        captureImageRepository = new DiskCaptureImageRepository(cacheDirectoryPath);

        // services
        captureImageService = new CaptureImageService(captureImageRepository);

        Path accountFilePath = PathValidator.validateAndConvert(
                appConfigService.getConfig().scannerConfig().accountFile().value());
        songRecordUploadService = new SongRecordUploadService(accountFilePath);

        // integrations
        songCaptureLinkingService =
                new SongCaptureLinkingService(captureService, songCaptureLinkService,
                        globalContext.songService, songTitleService);

        // use cases
        scannerScannerService = new ScannerScannerService(appConfigService, captureImageService,
                globalContext.captureRegionService, captureService, pixImageService,
                globalContext.songService, songTitleService, songTitleOcrServiceFactory,
                getSelectedCategorySet(), debug);

        scannerReviewService = new ScannerReviewService(captureImageService, captureService,
                songCaptureLinkService, songCaptureLinkingService, globalContext.songService);

        scannerAnalysisService =
                new ScannerAnalysisService(appConfigService, captureImageService, captureService,
                        pixImageService, songCaptureLinkService, globalContext.songService,
                        commonOcrServiceFactory);

        scannerUploadService =
                new ScannerUploadService(songCaptureLinkService, globalContext.songRecordService,
                        globalContext.songRecordStorageService, songRecordUploadService,
                        globalContext.songService, songTitleService, updatedSongRecordService);
    }

    public final Set<String> getSelectedCategorySet() {
        return Set.copyOf(selectedCategorySet);
    }
}
