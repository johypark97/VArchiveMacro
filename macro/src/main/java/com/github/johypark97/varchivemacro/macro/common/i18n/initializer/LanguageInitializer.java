package com.github.johypark97.varchivemacro.macro.common.i18n.initializer;

import com.github.johypark97.varchivemacro.macro.common.i18n.loader.LanguageConfigManager;
import com.github.johypark97.varchivemacro.macro.common.i18n.loader.LanguageResourceBundleLoader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageInitializer implements Supplier<ResourceBundle> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LanguageInitializer.class);

    private final LanguageConfigManager configManager;
    private final LanguageResourceBundleLoader resourceBundleLoader;
    private final Locale baseLocale;

    public LanguageInitializer(LanguageConfigManager configManager,
            LanguageResourceBundleLoader resourceBundleLoader, Locale baseLocale) {
        this.configManager = configManager;
        this.resourceBundleLoader = resourceBundleLoader;
        this.baseLocale = baseLocale;
    }

    protected void saveLocale(Locale locale) {
        try {
            configManager.save(locale);
        } catch (IOException e) {
            LOGGER.atError().setCause(e)
                    .log("An exception occurred while saving the changed locale to the configuration file. Changes will not be saved.");
        }
    }

    @Override
    public ResourceBundle get() {
        Locale locale = Locale.getDefault();

        try {
            locale = configManager.load();
        } catch (NoSuchFileException e) {
            LOGGER.atInfo()
                    .log("Locale configuration file not found. Use the system locale: {}", locale);
        } catch (Exception e) {
            LOGGER.atError().setCause(e)
                    .log("An exception occurred while loading locale configuration file. Use the system locale: {}",
                            locale);
        }

        ResourceBundle resourceBundle;
        try {
            resourceBundle = resourceBundleLoader.load(locale);
        } catch (MissingResourceException e) {
            LOGGER.atInfo().log("Unsupported locale: {}. Use {} instead.", locale, baseLocale);

            saveLocale(baseLocale);

            return resourceBundleLoader.load(baseLocale);
        }

        if (!locale.equals(resourceBundle.getLocale())) {
            LOGGER.atInfo()
                    .log("Unsupported locale is set. Change to supported locale: {} -> {}", locale,
                            resourceBundle.getLocale());

            saveLocale(resourceBundle.getLocale());
        }

        return resourceBundle;
    }
}
