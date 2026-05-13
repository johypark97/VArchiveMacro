package com.github.johypark97.varchivemacro.macro.core.scanner.api.record;

public enum Pattern {
    NM("NORMAL"),
    HD("HARD"),
    MX("MAXIMUM"),
    SC("SC");

    private final String fullName;

    Pattern(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
