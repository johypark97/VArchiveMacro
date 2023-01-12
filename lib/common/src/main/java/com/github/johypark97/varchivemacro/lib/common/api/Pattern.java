package com.github.johypark97.varchivemacro.lib.common.api;

public enum Pattern {
    NM("NORMAL"), HD("HARD"), MX("MAXIMUM"), SC("SC");

    private final String value;

    Pattern(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }
}
