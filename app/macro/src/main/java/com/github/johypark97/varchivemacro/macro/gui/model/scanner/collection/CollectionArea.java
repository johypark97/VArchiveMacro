package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface CollectionArea {
    enum Section {
        PLAY_COUNT(0), HIGH_SCORE(1), RATE(2), COMBO(3);

        private final int value;

        Section(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }


    enum Button {
        _4(0), _5(1), _6(2), _8(3);

        private final int value;

        Button(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }


    enum Pattern {
        NM(0), HD(1), MX(2), SC(3);

        private final int value;

        Pattern(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }

    default BufferedImage getSubimage(BufferedImage i, Rectangle r) {
        return i.getSubimage(r.x, r.y, r.width, r.height);
    }

    Rectangle getTitle();

    Rectangle getCell(Section section, Button button, Pattern pattern);

    Rectangle getRecord(Section section, Button button, Pattern pattern);

    Rectangle getMark(Section section, Button button, Pattern pattern);

    BufferedImage getTitle(BufferedImage image);

    BufferedImage getCell(BufferedImage image, Section section, Button button, Pattern pattern);

    BufferedImage getRecord(BufferedImage image, Section section, Button button, Pattern pattern);

    BufferedImage getMark(BufferedImage image, Section section, Button button, Pattern pattern);
}
