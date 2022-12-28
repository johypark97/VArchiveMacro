package com.github.johypark97.varchivemacro.macro.util;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.loadXmlProperties;

import java.io.IOException;
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
        try {
            properties = loadXmlProperties(getClass().getResource("/language_ko.xml"));
        } catch (IOException ignored) {
        }
    }
}
