package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.macro.model.AnalysisKey;
import java.util.function.Consumer;
import javafx.geometry.VerticalDirection;

public interface MacroService {
    void setupService(Consumer<Throwable> onThrow);

    void startMacro(AnalysisKey analysisKey, int count, int captureDelay, int captureDuration,
            int keyInputDuration, VerticalDirection direction);

    void stopMacro();
}
