package com.github.johypark97.varchivemacro.lib.common.api;

public enum Button {
    _4(4), _5(5), _6(6), _8(8);

    private final int value;

    Button(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
