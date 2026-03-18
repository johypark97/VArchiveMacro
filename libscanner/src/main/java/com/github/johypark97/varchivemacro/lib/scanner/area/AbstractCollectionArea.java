package com.github.johypark97.varchivemacro.lib.scanner.area;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

abstract class AbstractCollectionArea implements CollectionArea {
    private static BufferedImage crop(BufferedImage image, Rectangle r) {
        return image.getSubimage(r.x, r.y, r.width, r.height);
    }

    protected abstract Rectangle titleRectangle();

    protected abstract Point sectionAnchor();

    protected abstract Dimension nextSection();

    protected abstract Dimension nextButton();

    protected abstract Dimension nextPattern();

    protected abstract Rectangle cellRectangle();

    protected abstract Rectangle cellRateRectangle();

    protected abstract Rectangle cellComboMarkRectangle();

    protected abstract int scaleByResolution(int value);

    private Rectangle scaleRectangle(Rectangle r) {
        return new Rectangle(scaleByResolution(r.x), scaleByResolution(r.y),
                scaleByResolution(r.width), scaleByResolution(r.height));
    }

    private Point getAnchor(Section section, Button button, Pattern pattern) {
        Point anchor = sectionAnchor();
        Dimension nextSection = nextSection();
        Dimension nextButton = nextButton();
        Dimension nextPattern = nextPattern();

        int x = anchor.x;
        // x += nextSection.width * section.getWeight();
        x += nextButton.width * button.getWeight();
        // x += nextPattern.width * pattern.getWeight();

        int y = anchor.y;
        y += nextSection.height * section.getWeight();
        // y += nextButton.height * button.getWeight();
        y += nextPattern.height * pattern.getWeight();

        return new Point(x, y);
    }

    @Override
    public Rectangle getTitle() {
        Rectangle r = (Rectangle) titleRectangle().clone();

        return scaleRectangle(r);
    }

    @Override
    public Rectangle getCell(Section section, Button button, Pattern pattern) {
        Point p = getAnchor(section, button, pattern);

        Rectangle r = (Rectangle) cellRectangle().clone();
        r.translate(p.x, p.y);

        return scaleRectangle(r);
    }

    @Override
    public Rectangle getRate(Button button, Pattern pattern) {
        Point p = getAnchor(Section.SCORE, button, pattern);

        Rectangle r = (Rectangle) cellRateRectangle().clone();
        r.translate(p.x, p.y);

        return scaleRectangle(r);
    }

    @Override
    public Rectangle getComboMark(Button button, Pattern pattern) {
        Point p = getAnchor(Section.COMBO, button, pattern);

        Rectangle r = (Rectangle) cellComboMarkRectangle().clone();
        r.translate(p.x, p.y);

        return scaleRectangle(r);
    }

    @Override
    public BufferedImage getTitle(BufferedImage image) {
        return crop(image, getTitle());
    }

    @Override
    public BufferedImage getRate(BufferedImage image, Button button, Pattern pattern) {
        return crop(image, getRate(button, pattern));
    }

    @Override
    public BufferedImage getComboMark(BufferedImage image, Button button, Pattern pattern) {
        return crop(image, getComboMark(button, pattern));
    }
}
