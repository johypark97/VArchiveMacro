package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

abstract class AbstractCollectionArea implements CollectionArea {
    protected BufferedImage crop(BufferedImage image, Rectangle r) {
        return image.getSubimage(r.x, r.y, r.width, r.height);
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
