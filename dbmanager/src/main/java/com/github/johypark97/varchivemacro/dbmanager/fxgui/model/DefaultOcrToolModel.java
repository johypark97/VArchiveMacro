package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheCaptureService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheClassificationService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrGroundTruthGenerationService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheCaptureTask;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheClassificationTask;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrGroundTruthGenerationTask;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import java.awt.AWTException;
import java.nio.file.Path;
import java.util.Objects;

public class DefaultOcrToolModel implements OcrToolModel {
    @Override
    public OcrCacheCaptureService.Builder setupOcrCacheCaptureService() {
        return new OcrCacheCaptureService.Builder();
    }

    @Override
    public boolean startOcrCacheCaptureService(int captureDelay, int keyInputDelay,
            int keyInputDuration, Path outputPath) {
        OcrCacheCaptureService service = Objects.requireNonNull(
                ServiceManager.getInstance().get(OcrCacheCaptureService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            OcrCacheCaptureTask task;

            try {
                task = new OcrCacheCaptureTask();
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }

            task.captureDelay = captureDelay;
            task.keyInputDelay = keyInputDelay;
            task.keyInputDuration = keyInputDuration;
            task.outputPath = outputPath;

            return task;
        });

        service.reset();
        service.start();

        return true;
    }

    @Override
    public boolean stopOcrCacheCaptureService() {
        return ServiceManagerHelper.stopService(OcrCacheCaptureService.class);
    }

    @Override
    public OcrCacheClassificationService.Builder setupOcrCacheClassificationService() {
        return new OcrCacheClassificationService.Builder();
    }

    @Override
    public boolean startOcrCacheClassificationService(Path inputPath, Path outputPath) {
        OcrCacheClassificationService service = Objects.requireNonNull(
                ServiceManager.getInstance().get(OcrCacheClassificationService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            OcrCacheClassificationTask task = new OcrCacheClassificationTask();

            task.inputPath = inputPath;
            task.outputPath = outputPath;

            return task;
        });

        service.reset();
        service.start();

        return true;
    }

    @Override
    public boolean stopOcrCacheClassificationService() {
        return ServiceManagerHelper.stopService(OcrCacheClassificationService.class);
    }

    @Override
    public OcrGroundTruthGenerationService.Builder setupOcrGroundTruthGenerationService() {
        return new OcrGroundTruthGenerationService.Builder();
    }

    @Override
    public boolean startOcrGroundTruthGenerationService(Path inputPath, Path outputPath) {
        OcrGroundTruthGenerationService service = Objects.requireNonNull(
                ServiceManager.getInstance().get(OcrGroundTruthGenerationService.class));
        if (service.isRunning()) {
            return false;
        }

        service.setTaskConstructor(() -> {
            OcrGroundTruthGenerationTask task = new OcrGroundTruthGenerationTask();

            task.inputPath = inputPath;
            task.outputPath = outputPath;

            return task;
        });

        service.reset();
        service.start();

        return false;
    }

    @Override
    public boolean stopOcrGroundTruthGenerationService() {
        return ServiceManagerHelper.stopService(OcrGroundTruthGenerationService.class);
    }
}
