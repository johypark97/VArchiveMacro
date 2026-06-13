package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.impl;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.Region;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.Resolution;

public class CaptureRegionFHD extends AbstractCaptureRegion {
    private static final Resolution RESOLUTION = new Resolution(1920, 1080);

    private static final Region TITLE = new Region(612, 284, 800, 46);

    private static final Point SECTION_ANCHOR = new Point(929, 356);
    private static final Delta NEXT_SECTION = new Delta(0, 120);
    private static final Delta NEXT_BUTTON = new Delta(124, 0);
    private static final Delta NEXT_PATTERN = new Delta(0, 24);

    private static final Region CELL = new Region(0, 0, 120, 22);
    private static final Region CELL_RATE = new Region(0, 0, 90, 22);
    private static final Region CELL_COMBO_MARK = new Region(88, 0, 28, 22);

    @Override
    public Resolution getResolution() {
        return RESOLUTION;
    }

    @Override
    public Region getTitle() {
        return TITLE;
    }

    @Override
    protected Point getSectionAnchor() {
        return SECTION_ANCHOR;
    }

    @Override
    protected Delta getNextSection() {
        return NEXT_SECTION;
    }

    @Override
    protected Delta getNextButton() {
        return NEXT_BUTTON;
    }

    @Override
    protected Delta getNextPattern() {
        return NEXT_PATTERN;
    }

    @Override
    protected Region getRateRegion() {
        return CELL_RATE;
    }

    @Override
    protected Region getComboMarkRegion() {
        return CELL_COMBO_MARK;
    }
}
