package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.impl;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.Region;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.RegionButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model.RegionPattern;

abstract class AbstractCaptureRegion implements CaptureRegion {
    protected abstract Point getSectionAnchor();

    protected abstract Delta getNextSection();

    protected abstract Delta getNextButton();

    protected abstract Delta getNextPattern();

    protected abstract Region getRateRegion();

    protected abstract Region getComboMarkRegion();

    private Point getAnchor(Section section, RegionButton button, RegionPattern pattern) {
        Point anchor = getSectionAnchor();
        Delta nextSection = getNextSection();
        Delta nextButton = getNextButton();
        Delta nextPattern = getNextPattern();

        int x = anchor.x;
        // x += nextSection.dx() * section.getWeight();
        x += nextButton.dx() * button.getWeight();
        // x += nextPattern.dx() * pattern.getWeight();

        int y = anchor.y;
        y += nextSection.dy() * section.getWeight();
        // y += nextButton.dy() * button.getWeight();
        y += nextPattern.dy() * pattern.getWeight();

        return new Point(x, y);
    }

    @Override
    public Region getRate(RegionButton button, RegionPattern pattern) {
        Point p = getAnchor(Section.SCORE, button, pattern);
        return getRateRegion().move(p.x, p.y);
    }

    @Override
    public Region getMaxCombo(RegionButton button, RegionPattern pattern) {
        Point p = getAnchor(Section.COMBO, button, pattern);
        return getComboMarkRegion().move(p.x, p.y);
    }

    protected enum Section {
        COUNT(0),
        SCORE(1),
        RATE(2),
        COMBO(3);

        private final int weight;

        Section(int w) {
            weight = w;
        }

        public int getWeight() {
            return weight;
        }
    }

    protected record Point(int x, int y) {
    }

    protected record Delta(int dx, int dy) {
    }
}
