package com.github.johypark97.varchivemacro.macro.util;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.loadProperties;

import java.io.IOException;
import java.util.Properties;

public class BuildInfo {
    private static final String EMPTY_STRING = "";

    public static final String date;
    public static final String version;

    static {
        Properties properties = null;

        try {
            properties = loadProperties(BuildInfo.class.getResource("/build.properties"));
        } catch (IOException ignored) {
        }

        date = getValue(properties, "build.date");
        version = getValue(properties, "build.version");
    }

    private static String getValue(Properties properties, String key) {
        return (properties != null) ? properties.getProperty(key, EMPTY_STRING) : EMPTY_STRING;
    }
}
