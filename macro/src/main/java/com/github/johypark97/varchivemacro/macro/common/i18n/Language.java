package com.github.johypark97.varchivemacro.macro.common.i18n;

import com.github.johypark97.varchivemacro.macro.common.i18n.initializer.LanguageInitializer;
import com.github.johypark97.varchivemacro.macro.common.i18n.loader.DefaultLanguageConfigManager;
import com.github.johypark97.varchivemacro.macro.common.i18n.loader.DefaultLanguageResourceBundleLoader;
import com.github.johypark97.varchivemacro.macro.common.i18n.loader.LanguageConfigManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public enum Language {
    INSTANCE; // Singleton

    private static final Locale BASE_LOCALE = Locale.ENGLISH;
    private static final Path CONFIG_PATH = Path.of("locale.txt");
    private static final String RESOURCE_PROVIDER =
            "com.github.johypark97.varchivemacro.macro.Language";

    private final AtomicBoolean initialized = new AtomicBoolean();

    private LanguageConfigManager configManager;
    private ResourceBundle resourceBundle;

    public synchronized void initialize() {
        if (initialized.get()) {
            throw new IllegalStateException("Language is already initialized.");
        }

        configManager = new DefaultLanguageConfigManager(CONFIG_PATH);
        resourceBundle = new LanguageInitializer(configManager,
                new DefaultLanguageResourceBundleLoader(RESOURCE_PROVIDER), BASE_LOCALE).get();

        Locale.setDefault(resourceBundle.getLocale());

        initialized.set(true);
    }

    public ResourceBundle getResourceBundle() {
        checkInitialization();

        return resourceBundle;
    }

    public Locale getLocale() {
        return getResourceBundle().getLocale();
    }

    public String getString(String key) {
        return getResourceBundle().getString(key);
    }

    public String getFormatString(String key, Object... args) {
        return String.format(getString(key), args);
    }

    public void changeLocale(Locale locale) throws IOException {
        checkInitialization();

        configManager.save(locale);
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("Language is not initialized.");
        }
    }
}
