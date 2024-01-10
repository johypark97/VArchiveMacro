package com.github.johypark97.varchivemacro.lib.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

public abstract class BaseXmlResourceBundleProvider extends AbstractXmlResourceBundleProvider {
    protected abstract String getBaseName(String baseName);

    protected abstract InputStream openResourceStream(String resourceName);

    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        String baseName0 = getBaseName(baseName);
        String bundleName = toBundleName(baseName0, locale);
        String resourceName = toResourceName(bundleName);

        try (InputStream stream = openResourceStream(resourceName)) {
            return (stream != null) ? new XmlResourceBundle(stream) : null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
