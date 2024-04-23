package com.github.johypark97.varchivemacro.macro.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Language {
    private static final String RESOURCE_PROVIDER =
            "com.github.johypark97.varchivemacro.macro.Language";

    private static final Path CONFIG_PATH = Path.of("locale");

    private final AtomicReference<ResourceBundle> resourceBundleReference = new AtomicReference<>();

    public static Language getInstance() {
        return Singleton.INSTANCE;
    }

    public static Locale loadLocale(Path path) throws IOException {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.findFirst().map(Locale::forLanguageTag).orElse(Locale.ROOT);
        }
    }

    public static void saveLocale(Locale locale) throws IOException {
        Files.writeString(CONFIG_PATH, locale.toLanguageTag());
    }

    public synchronized void initialize() {
        if (resourceBundleReference.get() != null) {
            throw new IllegalStateException("Language is already initialized.");
        }

        Locale locale = Locale.getDefault();
        try {
            locale = loadLocale(CONFIG_PATH);
        } catch (IOException ignored) {
        }

        ResourceBundle resourceBundle;
        try {
            resourceBundle = ResourceBundle.getBundle(RESOURCE_PROVIDER, locale);
            Locale.setDefault(resourceBundle.getLocale());
        } catch (MissingResourceException e) {
            Locale.setDefault(Locale.ENGLISH);
            resourceBundle = ResourceBundle.getBundle(RESOURCE_PROVIDER);
        }

        try {
            saveLocale(resourceBundle.getLocale());
        } catch (IOException ignored) {
        }

        resourceBundleReference.set(resourceBundle);
    }

    public ResourceBundle getResourceBundle() {
        ResourceBundle resourceBundle = resourceBundleReference.get();
        if (resourceBundle == null) {
            throw new IllegalStateException("Language is not initialized.");
        }

        return resourceBundle;
    }

    public Locale getLocale() {
        ResourceBundle resourceBundle = getResourceBundle();

        return resourceBundle.getLocale();
    }

    public String getString(String key) {
        ResourceBundle resourceBundle = getResourceBundle();

        return resourceBundle.getString(key);
    }

    public String getFormatString(String key, Object... args) {
        return String.format(getString(key), args);
    }

    private static class Singleton {
        private static final Language INSTANCE = new Language();
    }
}
