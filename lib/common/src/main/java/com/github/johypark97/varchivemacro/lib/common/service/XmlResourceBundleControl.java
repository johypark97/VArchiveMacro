package com.github.johypark97.varchivemacro.lib.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class XmlResourceBundleControl extends ResourceBundle.Control {
    public static final String FORMAT_XML = "xml";

    @Override
    public List<String> getFormats(String baseName) {
        return List.of(FORMAT_XML);
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
            ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {
        if (!FORMAT_XML.equals(format)) {
            return null;
        }

        String bundleName = toBundleName(baseName, locale);
        String resourceName = toResourceName(bundleName, FORMAT_XML);

        try (InputStream stream = loader.getResourceAsStream(resourceName)) {
            if (stream == null) {
                return null;
            }

            XmlResourceBundle xmlResourceBundle = new XmlResourceBundle();
            xmlResourceBundle.loadFromXML(stream);

            return xmlResourceBundle;
        }
    }
}
