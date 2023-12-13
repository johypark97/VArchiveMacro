package com.github.johypark97.varchivemacro.lib.common.area;

import com.github.johypark97.varchivemacro.lib.common.Enums.Button;
import com.github.johypark97.varchivemacro.lib.common.Enums.Pattern;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

class CollectionAreaHD extends AbstractCollectionArea {
    private static final Rectangle TITLE = new Rectangle(409, 191, 532, 33);

    private static final Dimension NEXT_BUTTON = new Dimension(83, 0);
    private static final Dimension NEXT_PATTERN = new Dimension(0, 20);
    private static final Dimension NEXT_SECTION = new Dimension(0, 99);
    private static final Point ANCHOR = new Point(619, 245);

    private static final Rectangle CELL = new Rectangle(0, 0, 80, 19);
    private static final Rectangle CELL_COMBO_MARK = new Rectangle(58, 0, 21, 19);
    private static final Rectangle CELL_RATE = new Rectangle(22, 0, 35, 19);

    public static final Dimension RESOLUTION = new Dimension(1280, 720);

    private Point getAnchor(Section section, Button button, Pattern pattern) {
        int x = ANCHOR.x;
        // x += NEXT_SECTION.width * section.getWeight();
        x += NEXT_BUTTON.width * button.getWeight();
        // x += NEXT_PATTERN.width * pattern.getWeight();

        int y = ANCHOR.y;
        y += NEXT_SECTION.height * section.getWeight();
        // y += NEXT_BUTTON.height * button.getWeight();
        y += NEXT_PATTERN.height * pattern.getWeight();

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
