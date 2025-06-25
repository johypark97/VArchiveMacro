package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

public enum RecordPattern {
    NORMAL(0),
    HARD(1),
    MAXIMUM(2),
    SC(3);

    private final int weight;

    RecordPattern(int w) {
        weight = w;
    }

    public int getWeight() {
        return weight;
    }
}
