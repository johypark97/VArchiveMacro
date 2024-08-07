package com.github.johypark97.varchivemacro.macro.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class BuildInfo {
    public static final String date;
    public static final String version;

    static {
        Properties properties = new Properties();

        URL url = BuildInfo.class.getResource("/build.properties");
        if (url != null) {
            try (InputStream stream = url.openStream()) {
                properties.load(stream);
            } catch (IOException ignored) {
            }
        }

        date = properties.getProperty("build.date", "");
        version = properties.getProperty("build.version", "");
    }
}
