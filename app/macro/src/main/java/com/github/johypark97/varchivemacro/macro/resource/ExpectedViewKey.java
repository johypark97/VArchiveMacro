package com.github.johypark97.varchivemacro.macro.resource;

import java.util.Arrays;
import java.util.List;

public enum ExpectedViewKey {
    // @formatter:off
    ALWAYS_ON_TOP("alwaysOnTop"),
    WINDOW_TITLE("windowTitle"),
    ;
    // @formatter:on

    private static final String PREFIX = "expected.view.";

    private final String value;

    ExpectedViewKey(String s) {
        value = s;
    }

    public static List<String> valueList() {
        return Arrays.stream(values()).map(ExpectedViewKey::toString).toList();
    }

    @Override
    public String toString() {
        return PREFIX + value;
    }
}
