package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

public enum RegionButton {
    B4(0),
    B5(1),
    B6(2),
    B8(3);

    private final int weight;

    RegionButton(int w) {
        weight = w;
    }

    public int getWeight() {
        return weight;
    }
}
