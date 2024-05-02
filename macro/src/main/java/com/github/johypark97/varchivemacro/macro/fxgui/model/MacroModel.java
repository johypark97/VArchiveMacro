package com.github.johypark97.varchivemacro.macro.fxgui.model;

import java.util.function.Consumer;
import javafx.geometry.VerticalDirection;

public interface MacroModel {
    void setupService(Consumer<Throwable> onThrow);

    void startMacro(AnalysisKey analysisKey, int count, int captureDelay, int captureDuration,
            int keyInputDuration, VerticalDirection direction);

    void stopMacro();

    enum AnalysisKey {
        F11, F12, HOME, INSERT
    }
}
