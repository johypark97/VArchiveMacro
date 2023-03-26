package com.github.johypark97.varchivemacro.macro.util;

import com.github.johypark97.varchivemacro.lib.common.resource.XmlResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class Language {
    private static final Path CONFIG_PATH = Path.of("locale");
    private static final String RESOURCE_PATH = "/strings/language";

    private static Language instance;

    private Locale locale;
    private Properties properties;

    public static synchronized void init() throws IOException {
        instance = new Language();

        LanguageLoader loader = new LanguageLoader(RESOURCE_PATH);
        try {
            instance.properties = loader.load(loadLocale());
        } catch (MissingResourceException e) {
            instance.properties = loader.load(Locale.ENGLISH);
        }
        instance.locale = loader.getLocale();

        testLocale();
        saveLocale(loader.getLocale());
    }

    protected static Locale loadLocale() throws IOException {
        Locale locale = Locale.getDefault();

        if (Files.exists(CONFIG_PATH)) {
            try (Stream<String> stream = Files.lines(CONFIG_PATH)) {
                Optional<String> firstLine = stream.findFirst();
                if (firstLine.isPresent()) {
                    locale = Locale.forLanguageTag(firstLine.get());
                }
            }
        }

        return locale;
    }

    public static void saveLocale(Locale locale) {
        try {
            Files.writeString(CONFIG_PATH, locale.toLanguageTag());
        } catch (Exception ignored) {
        }
    }

    protected static void testLocale() {
        List<String> keys = new LinkedList<>();
        keys.addAll(MacroViewKey.valueList());
        keys.addAll(MacroPresenterKey.valueList());
        keys.addAll(ExpectedViewKey.valueList());
        keys.addAll(ScannerTaskViewKey.valueList());

        for (String key : keys) {
            if (instance.properties.get(key) == null) {
                throw new IllegalStateException(
                        String.format("Language key '%s' does not exist in locale '%s'", key,
                                instance.locale));
            }
        }
    }

    public static Language getInstance() {
        return instance;
    }

    public Locale getLocale() {
        return locale;
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String get(MacroViewKey key) {
        return get(key.toString());
    }

    public String get(MacroPresenterKey key) {
        return get(key.toString());
    }

    public String get(ExpectedViewKey key) {
        return get(key.toString());
    }

    public String get(ScannerTaskViewKey key) {
        return get(key.toString());
    }
}


class LanguageLoader extends XmlResourceLoader {
    public LanguageLoader(String path) {
        super(path);
    }

    @Override
    public Properties getProperty(String name) throws IOException {
        URL url = getClass().getResource(name);
        if (url == null) {
            return null; // NOPMD
        }

        try (InputStream stream = url.openStream()) {
            Properties properties = new Properties();
            properties.loadFromXML(stream);
            return properties;
        }
    }
}
