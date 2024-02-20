package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheCaptureService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheClassificationService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrGroundTruthGenerationService;
import java.nio.file.Path;

public interface OcrToolModel {
    OcrCacheCaptureService.Builder setupOcrCacheCaptureService();

    boolean startOcrCacheCaptureService(int captureDelay, int keyInputDelay, int keyInputDuration,
            Path outputPath);

    boolean stopOcrCacheCaptureService();

    OcrCacheClassificationService.Builder setupOcrCacheClassificationService();

    boolean startOcrCacheClassificationService(Path inputPath, Path outputPath);

    boolean stopOcrCacheClassificationService();

    OcrGroundTruthGenerationService.Builder setupOcrGroundTruthGenerationService();

    boolean startOcrGroundTruthGenerationService(Path inputPath, Path outputPath);

    boolean stopOcrGroundTruthGenerationService();
}
