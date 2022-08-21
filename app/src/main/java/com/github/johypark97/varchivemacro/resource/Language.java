package com.github.johypark97.varchivemacro.resource;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Language {
    // singleton
    private Language() {}

    private static final class LanguageInstance {
        private static final Language instance = new Language();
    }

    public static Language getInstance() {
        return LanguageInstance.instance;
    }

    // instance
    private static final String BASE_NAME = "language";

    private volatile Locale locale = Locale.getDefault();
    private ResourceBundle bundle;

    public String get(String key) {
        if (bundle == null) {
            synchronized (this) {
                if (bundle == null)
                    bundle = createBundle();
            }
        }

        return bundle.getString(key);
    }

    public synchronized void changeLocale(Locale locale) {
        this.locale = locale;
        bundle = createBundle();
    }

    private ResourceBundle createBundle() {
        try {
            return ResourceBundle.getBundle(BASE_NAME, locale);
        } catch (MissingResourceException e) {
            return ResourceBundle.getBundle(BASE_NAME, Locale.KOREAN);
        }
    }
}
