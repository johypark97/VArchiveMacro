package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheCaptureService;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.OcrCacheCaptureService.Builder;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheCaptureTask;
import java.awt.AWTException;
import java.nio.file.Path;
import java.util.Objects;

public class DefaultOcrToolModel implements OcrToolModel {
    @Override
    public Builder setupOcrCacheCaptureService() {
        return new Builder();
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
        return ModelHelper.stopService(OcrCacheCaptureService.class);
    }
}
