package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

public interface CaptureRegion {
    Resolution getResolution();

    Region getTitle();

    Region getRate(RegionButton button, RegionPattern pattern);

    Region getMaxCombo(RegionButton button, RegionPattern pattern);
}
