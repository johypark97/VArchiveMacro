package com.github.johypark97.varchivemacro.macro.util;

import java.util.Arrays;
import java.util.List;

public enum ScannerTaskViewKey {
    // @formatter:off
    RECORD_IMAGE("recordImage"),
    TAB_FULL_IMAGE("tab.fullImage"),
    TAB_RESULT("tab.result"),
    TITLE_IMAGE("titleImage"),
    WINDOW_TITLE("windowTitle"),
    ;
    // @formatter:on

    private static final String PREFIX = "scannerTask.view.";

    private final String value;

    ScannerTaskViewKey(String s) {
        value = s;
    }

    public static List<String> valueList() {
        return Arrays.stream(values()).map(ScannerTaskViewKey::toString).toList();
    }

    @Override
    public String toString() {
        return PREFIX + value;
    }
}
