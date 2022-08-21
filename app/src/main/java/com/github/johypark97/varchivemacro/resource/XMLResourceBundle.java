// reference: https://docs.oracle.com/javase/8/docs/api/java/util/ResourceBundle.Control.html

package com.github.johypark97.varchivemacro.resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class XMLResourceBundle extends ResourceBundle {
    private Properties props;

    XMLResourceBundle(InputStream stream) throws IOException {
        props = new Properties();
        props.loadFromXML(stream);
    }

    @Override
    protected Object handleGetObject(String key) {
        return props.getProperty(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        Set<String> handleKeys = props.stringPropertyNames();
        return Collections.enumeration(handleKeys);
    }

    public static class Control extends ResourceBundle.Control {
        private static final String FORMAT_XML = "xml";

        @Override
        public List<String> getFormats(String baseName) {
            if (baseName == null)
                throw new NullPointerException();
            return Arrays.asList(FORMAT_XML);
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format,
                ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            if (baseName == null || locale == null || format == null || loader == null)
                throw new NullPointerException();

            ResourceBundle bundle = null;
            if (format.equals(FORMAT_XML)) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, format);
                InputStream stream = null;

                if (!reload)
                    stream = loader.getResourceAsStream(resourceName);
                else {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                }

                if (stream != null) {
                    BufferedInputStream bis = new BufferedInputStream(stream);
                    bundle = new XMLResourceBundle(bis);
                    bis.close();
                }
            }

            return bundle;
        }
    }
}
