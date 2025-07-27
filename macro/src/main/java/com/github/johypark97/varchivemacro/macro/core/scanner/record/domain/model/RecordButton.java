package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

public enum RecordButton {
    B4(0, "4B"),
    B5(1, "5B"),
    B6(2, "6B"),
    B8(3, "8B");

    private final String buttonName;
    private final int weight;

    RecordButton(int w, String s) {
        buttonName = s;
        weight = w;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return buttonName;
    }
}
