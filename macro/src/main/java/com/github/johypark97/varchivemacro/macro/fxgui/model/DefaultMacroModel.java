package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManagerHelper;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.MacroService;
import com.github.johypark97.varchivemacro.macro.fxgui.model.service.task.MacroTask;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.VerticalDirection;

public class DefaultMacroModel implements MacroModel {
    @Override
    public void setupService(Consumer<Throwable> onThrow) {
        MacroService service = ServiceManager.getInstance().create(MacroService.class);
        if (service == null) {
            throw new IllegalStateException("MacroService has already been created.");
        }

        service.setOnFailed(event -> onThrow.accept(event.getSource().getException()));
    }

    @Override
    public void startMacro(AnalysisKey analysisKey, int count, int captureDelay,
            int captureDuration, int keyInputDuration, VerticalDirection direction) {
        if (ServiceManager.getInstance().isRunningAny()) {
            return;
        }

        MacroService service =
                Objects.requireNonNull(ServiceManager.getInstance().get(MacroService.class));

        service.setTaskConstructor(
                () -> new MacroTask(analysisKey, count, captureDelay, captureDuration,
                        keyInputDuration, direction));

        service.reset();
        service.start();
    }

    @Override
    public void stopMacro() {
        ServiceManagerHelper.stopService(MacroService.class);
    }
}
