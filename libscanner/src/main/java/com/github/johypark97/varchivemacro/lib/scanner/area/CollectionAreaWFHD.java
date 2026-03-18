package com.github.johypark97.varchivemacro.lib.scanner.area;

import com.google.common.math.IntMath;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

class CollectionAreaWFHD extends AbstractCollectionArea {
    public static final Dimension RESOLUTION_MINIMUM = new Dimension(2560, 1080);
    public static final Point RESOLUTION_RATIO = new Point(64, 27);

    private static final Rectangle TITLE = new Rectangle(932, 284, 800, 46);

    private static final Point SECTION_ANCHOR = new Point(1249, 356);
    private static final Dimension NEXT_SECTION = new Dimension(0, 120);
    private static final Dimension NEXT_BUTTON = new Dimension(124, 0);
    private static final Dimension NEXT_PATTERN = new Dimension(0, 24);

    private static final Rectangle CELL = new Rectangle(0, 0, 120, 22);
    private static final Rectangle CELL_RATE = new Rectangle(0, 0, 90, 22);
    private static final Rectangle CELL_COMBO_MARK = new Rectangle(88, 0, 28, 22);

    private final int scaleFactorA;
    private final int scaleFactorB;

    public CollectionAreaWFHD(Dimension resolution) {
        int gcd = IntMath.gcd(RESOLUTION_MINIMUM.width, resolution.width);

        scaleFactorA = RESOLUTION_MINIMUM.width / gcd;
        scaleFactorB = resolution.width / gcd;
    }

    @Override
    protected Rectangle titleRectangle() {
        return TITLE;
    }

    @Override
    protected Point sectionAnchor() {
        return SECTION_ANCHOR;
    }

    @Override
    protected Dimension nextSection() {
        return NEXT_SECTION;
    }

    @Override
    protected Dimension nextButton() {
        return NEXT_BUTTON;
    }

    @Override
    protected Dimension nextPattern() {
        return NEXT_PATTERN;
    }

    @Override
    protected Rectangle cellRectangle() {
        return CELL;
    }

    @Override
    protected Rectangle cellRateRectangle() {
        return CELL_RATE;
    }

    @Override
    protected Rectangle cellComboMarkRectangle() {
        return CELL_COMBO_MARK;
    }

    @Override
    protected int scaleByResolution(int value) {
        return (int) Math.round((double) value * scaleFactorB / scaleFactorA);
    }
}
