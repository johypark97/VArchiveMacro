package com.github.johypark97.varchivemacro.lib.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleProvider;

public abstract class AbstractXmlResourceBundleProvider implements ResourceBundleProvider {
    protected abstract String getBaseName(String baseName);

    protected abstract InputStream openResourceStream(String resourceName);

    @Override
    public ResourceBundle getBundle(String baseName, Locale locale) {
        XmlResourceBundleControl control = new XmlResourceBundleControl();

        String baseName0 = getBaseName(baseName);
        String bundleName = control.toBundleName(baseName0, locale);
        String resourceName =
                control.toResourceName(bundleName, XmlResourceBundleControl.FORMAT_XML);

        try (InputStream stream = openResourceStream(resourceName)) {
            if (stream == null) {
                return null;
            }

            XmlResourceBundle xmlResourceBundle = new XmlResourceBundle();
            xmlResourceBundle.loadFromXML(stream);

            return xmlResourceBundle;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
