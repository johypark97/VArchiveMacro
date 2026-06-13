package com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.model;

public enum RegionPattern {
    NORMAL(0),
    HARD(1),
    MAXIMUM(2),
    SC(3);

    private final int weight;

    RegionPattern(int w) {
        weight = w;
    }

    public int getWeight() {
        return weight;
    }
}
