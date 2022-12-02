package com.github.johypark97.varchivemacro.util;

import java.util.ResourceBundle;

public class BuildInfo {
    private static final String EMPTY_STRING = "";

    public static final String date;
    public static final String version;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("build");
        date = getValue(bundle, "build.date");
        version = getValue(bundle, "build.version");
    }

    private static String getValue(ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return EMPTY_STRING;
        }
    }
}
