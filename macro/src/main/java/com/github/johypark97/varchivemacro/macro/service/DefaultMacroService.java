package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.macro.model.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.service.fxservice.MacroFxService;
import com.github.johypark97.varchivemacro.macro.service.task.MacroTask;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.VerticalDirection;

public class DefaultMacroService implements MacroService {
    @Override
    public void setupService(Consumer<Throwable> onThrow) {
        MacroFxService service = ServiceManager.getInstance().create(MacroFxService.class);
        if (service == null) {
            throw new IllegalStateException("MacroFxService has already been created.");
        }

        service.setOnFailed(event -> onThrow.accept(event.getSource().getException()));
    }

    @Override
    public void startMacro(AnalysisKey analysisKey, int count, int captureDelay,
            int captureDuration, int keyInputDuration, VerticalDirection direction) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        MacroFxService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(MacroFxService.class));

        service.setTaskConstructor(
                () -> new MacroTask(analysisKey, count, captureDelay, captureDuration,
                        keyInputDuration, direction));

        service.reset();
        service.start();
    }

    @Override
    public void stopMacro() {
        ServiceManagerHelper.stopService(MacroFxService.class);
    }
}
