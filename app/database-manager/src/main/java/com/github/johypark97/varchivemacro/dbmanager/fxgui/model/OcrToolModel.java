package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheCaptureService.Builder;
import java.nio.file.Path;

public interface OcrToolModel {
    Builder setupOcrCacheCaptureService();

    boolean startOcrCacheCaptureService(int captureDelay, int keyInputDelay, int keyInputDuration,
            Path outputPath);

    boolean stopOcrCacheCaptureService();
}
