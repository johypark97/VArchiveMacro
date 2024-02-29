package com.github.johypark97.varchivemacro.lib.scanner.area;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

abstract class AbstractCollectionArea implements CollectionArea {
    protected BufferedImage crop(BufferedImage image, Rectangle r) {
        return image.getSubimage(r.x, r.y, r.width, r.height);
    }

    @Override
    public Rectangle getTitle_training() {
        Rectangle r = getTitle();
        r.grow(TITLE_MARGIN, TITLE_MARGIN);

        return r;
    }

    @Override
    public BufferedImage getTitle(BufferedImage image) {
        return crop(image, getTitle());
    }

    @Override
    public BufferedImage getTitle_training(BufferedImage image) {
        return crop(image, getTitle_training());
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
