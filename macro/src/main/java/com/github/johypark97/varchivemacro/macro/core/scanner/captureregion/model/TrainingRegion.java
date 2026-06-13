package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

public class TrainingRegion implements CaptureRegion {
    private static final int TRAINING_MARGIN = 10;

    private final CaptureRegion captureRegion;

    public TrainingRegion(CaptureRegion captureRegion) {
        this.captureRegion = captureRegion;
    }

    public Region getTitleRegion_withTrainingMargin() {
        return captureRegion.getTitle().expand(TRAINING_MARGIN);
    }

    @Override
    public Resolution getResolution() {
        return captureRegion.getResolution();
    }

    @Override
    public Region getTitle() {
        return captureRegion.getTitle();
    }

    @Override
    public Region getRate(RegionButton button, RegionPattern pattern) {
        return captureRegion.getRate(button, pattern);
    }

    @Override
    public Region getMaxCombo(RegionButton button, RegionPattern pattern) {
        return captureRegion.getMaxCombo(button, pattern);
    }
}
