package com.github.johypark97.varchivemacro.macro.common.i18n.loader;

import com.github.johypark97.varchivemacro.lib.common.service.XmlResourceBundleControl;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.spi.LanguageProviderImpl;
import java.util.Locale;
import java.util.ResourceBundle;

public class DefaultLanguageResourceBundleLoader implements LanguageResourceBundleLoader {
    private final String resourceProvider;

    public DefaultLanguageResourceBundleLoader(String resourceProvider) {
        this.resourceProvider = resourceProvider;
    }

    private ResourceBundle loadUsingServiceProvider(Locale locale) {
        return ResourceBundle.getBundle(resourceProvider, locale);
    }

    private ResourceBundle loadUsingControl(Locale locale) {
        return ResourceBundle.getBundle(LanguageProviderImpl.BASE_NAME, locale,
                new XmlResourceBundleControl());
    }

    @Override
    public ResourceBundle load(Locale locale) {
        return Language.class.getModule().isNamed()
                ? loadUsingServiceProvider(locale)
                : loadUsingControl(locale);
    }
}
