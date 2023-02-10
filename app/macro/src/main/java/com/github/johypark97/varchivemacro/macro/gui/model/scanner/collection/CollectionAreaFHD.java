package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class CollectionAreaFHD implements CollectionArea {
    public static final Dimension RESOLUTION = new Dimension(1920, 1080);

    private static final Rectangle TITLE = new Rectangle(612, 282, 810, 60);

    private static final Dimension NEXT_BUTTON = new Dimension(124, 0);
    private static final Dimension NEXT_PATTERN = new Dimension(0, 30);
    private static final Dimension NEXT_SECTION = new Dimension(0, 148);
    private static final Point SECTION_ANCHOR = new Point(929, 368);

    private static final Rectangle CELL = new Rectangle(0, 0, 120, 28);
    private static final Rectangle CELL_MARK = new Rectangle(84, 0, 36, 28);
    private static final Rectangle CELL_RECORD = new Rectangle(0, 0, 84, 28);

    private Point getAnchor(Section section, Button button, Pattern pattern) {
        int x = SECTION_ANCHOR.x;
        // x += NEXT_SECTION.width * section.getValue();
        x += NEXT_BUTTON.width * button.getValue();
        // x += NEXT_PATTERN.width * pattern.getValue();

        int y = SECTION_ANCHOR.y;
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
    public Rectangle getRecord(Section section, Button button, Pattern pattern) {
        Point p = getAnchor(section, button, pattern);

        Rectangle r = (Rectangle) CELL_RECORD.clone();
        r.translate(p.x, p.y);

        return r;
    }

    @Override
    public Rectangle getMark(Section section, Button button, Pattern pattern) {
        Point p = getAnchor(section, button, pattern);

        Rectangle r = (Rectangle) CELL_MARK.clone();
        r.translate(p.x, p.y);

        return r;
    }

    @Override
    public BufferedImage getTitle(BufferedImage image) {
        return getSubimage(image, getTitle());
    }

    @Override
    public BufferedImage getCell(BufferedImage image, Section section, Button button,
            Pattern pattern) {
        return getSubimage(image, getCell(section, button, pattern));
    }

    @Override
    public BufferedImage getRecord(BufferedImage image, Section section, Button button,
            Pattern pattern) {
        return getSubimage(image, getRecord(section, button, pattern));
    }

    @Override
    public BufferedImage getMark(BufferedImage image, Section section, Button button,
            Pattern pattern) {
        return getSubimage(image, getMark(section, button, pattern));
    }
}
