package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

public enum RecordButton {
    B4(0),
    B5(1),
    B6(2),
    B8(3);

    private final int weight;

    RecordButton(int w) {
        weight = w;
    }

    public int getWeight() {
        return weight;
    }
}
