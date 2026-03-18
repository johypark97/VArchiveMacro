package com.github.johypark97.varchivemacro.lib.scanner.area;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public interface CollectionArea {
    Rectangle getTitle();

    Rectangle getCell(Section section, Button button, Pattern pattern);

    Rectangle getRate(Button button, Pattern pattern);

    Rectangle getComboMark(Button button, Pattern pattern);

    BufferedImage getTitle(BufferedImage image);

    BufferedImage getRate(BufferedImage image, Button button, Pattern pattern);

    BufferedImage getComboMark(BufferedImage image, Button button, Pattern pattern);

    enum Section {
        COUNT(0), SCORE(1), RATE(2), COMBO(3);

        private final int weight;

        Section(int w) {
            weight = w;
        }

        public int getWeight() {
            return weight;
        }
    }
}
