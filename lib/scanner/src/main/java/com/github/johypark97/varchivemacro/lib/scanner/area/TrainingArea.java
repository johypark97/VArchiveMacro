package com.github.johypark97.varchivemacro.lib.scanner.area;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class TrainingArea {
    public static final int TRAINING_MARGIN = 10;

    private final CollectionArea area;

    public TrainingArea(CollectionArea area) {
        this.area = area;
    }

    public static BufferedImage cropTrainingMargin(BufferedImage image) {
        Rectangle r = new Rectangle(image.getWidth(), image.getHeight());
        r.grow(-TRAINING_MARGIN, -TRAINING_MARGIN);

        return image.getSubimage(r.x, r.y, r.width, r.height);
    }

    public Rectangle getTrainingTitle() {
        Rectangle r = area.getTitle();
        r.grow(TRAINING_MARGIN, TRAINING_MARGIN);

        return r;
    }

    public BufferedImage getTrainingTitle(BufferedImage image) {
        Rectangle r = getTrainingTitle();
        return image.getSubimage(r.x, r.y, r.width, r.height);
    }
}
