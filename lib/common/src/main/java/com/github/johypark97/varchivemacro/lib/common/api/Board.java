package com.github.johypark97.varchivemacro.lib.common.api;

public enum Board {
    _1("1"), _2("2"), _3("3"), _4("4"), _5("5"), _6("6"), _7("7"), _8("8"), _9("9"), _10("10"), _11(
            "11"), MX("MX"), SC("SC"), SC5("SC5"), SC10("SC10"), SC15("SC15");

    private final String value;

    Board(String s) {
        value = s;
    }

    @Override
    public String toString() {
        return value;
    }
}
