package com.github.johypark97.varchivemacro.macro.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Language {
    // singleton
    private Language() {
    }

    private static final class LanguageInstance {
        private static final Language instance = new Language();
    }

    public static Language getInstance() {
        return LanguageInstance.instance;
    }

    // instance
    private static final String DEFAULT_VALUE = "ERROR";

    private volatile Properties properties;

    public String get(String key) {
        if (properties == null) {
            synchronized (this) {
                if (properties == null) {
                    create();
                }
            }
        }

        return properties.getProperty(key, DEFAULT_VALUE);
    }

    private void create() {
        properties = new Properties();

        URL url = getClass().getResource("/language_ko.xml");
        if (url == null) {
            return;
        }

        try (InputStream stream = url.openStream()) {
            properties.loadFromXML(stream);
        } catch (IOException ignored) {
        }
    }
}
