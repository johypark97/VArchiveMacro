package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface CollectionArea {
    enum Section {
        COUNT(0), SCORE(1), RATE(2), COMBO(3);

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

    Rectangle getTitle();

    Rectangle getCell(Section section, Button button, Pattern pattern);

    Rectangle getRate(Button button, Pattern pattern);

    Rectangle getComboMark(Button button, Pattern pattern);

    BufferedImage getTitle(BufferedImage image);

    BufferedImage getRate(BufferedImage image, Button button, Pattern pattern);

    BufferedImage getComboMark(BufferedImage image, Button button, Pattern pattern);
}
