package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import java.awt.Rectangle;

public interface CaptureRegion {
    Rectangle getTitle();

    Rectangle getRate(RecordButton button, RecordPattern pattern);

    Rectangle getMaxCombo(RecordButton button, RecordPattern pattern);
}
