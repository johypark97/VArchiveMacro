package com.github.johypark97.varchivemacro.macro.core.scanner.collection;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

class CollectionAreaFHD extends AbstractCollectionArea {
    private static final Rectangle TITLE = new Rectangle(612, 282, 810, 60);

    private static final Dimension NEXT_BUTTON = new Dimension(124, 0);
    private static final Dimension NEXT_PATTERN = new Dimension(0, 30);
    private static final Dimension NEXT_SECTION = new Dimension(0, 148);
    private static final Point ANCHOR = new Point(929, 368);

    private static final Rectangle CELL = new Rectangle(0, 0, 120, 28);
    private static final Rectangle CELL_COMBO_MARK = new Rectangle(87, 0, 31, 28);
    private static final Rectangle CELL_RATE = new Rectangle(33, 0, 53, 28);

    public static final Dimension RESOLUTION = new Dimension(1920, 1080);

    private Point getAnchor(Section section, Button button, Pattern pattern) {
        int x = ANCHOR.x;
        // x += NEXT_SECTION.width * section.getValue();
        x += NEXT_BUTTON.width * button.getValue();
        // x += NEXT_PATTERN.width * pattern.getValue();

        int y = ANCHOR.y;
        y += NEXT_SECTION.height * section.getValue();
        // y += NEXT_BUTTON.height * button.getValue();
        y += NEXT_PATTERN.height * pattern.getValue();

        return new Point(x, y);
    }

    @Override
    public Rectangle getTitle() {
        return (Rectangle) TITLE.clone();
    }

    @Override
    public Rectangle getCell(Section section, Button button, Pattern pattern) {
        Point p = getAnchor(section, button, pattern);

        Rectangle r = (Rectangle) CELL.clone();
        r.translate(p.x, p.y);

        return r;
    }

    @Override
    public Rectangle getRate(Button button, Pattern pattern) {
        Point p = getAnchor(Section.RATE, button, pattern);

        Rectangle r = (Rectangle) CELL_RATE.clone();
        r.translate(p.x, p.y);

        return r;
    }

    @Override
    public Rectangle getComboMark(Button button, Pattern pattern) {
        Point p = getAnchor(Section.COMBO, button, pattern);

        Rectangle r = (Rectangle) CELL_COMBO_MARK.clone();
        r.translate(p.x, p.y);

        return r;
    }
}
