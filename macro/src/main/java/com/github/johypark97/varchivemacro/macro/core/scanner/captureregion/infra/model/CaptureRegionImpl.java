package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.infra.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import java.awt.Rectangle;

public class CaptureRegionImpl implements CaptureRegion {
    public final Rectangle[][] maxComboArray = new Rectangle[4][4];
    public final Rectangle[][] rateArray = new Rectangle[4][4];

    public Rectangle title;

    @Override
    public Rectangle getTitle() {
        return title;
    }

    @Override
    public Rectangle getRate(RecordButton button, RecordPattern pattern) {
        return rateArray[button.getWeight()][pattern.getWeight()];
    }

    @Override
    public Rectangle getMaxCombo(RecordButton button, RecordPattern pattern) {
        return maxComboArray[button.getWeight()][pattern.getWeight()];
    }
}
