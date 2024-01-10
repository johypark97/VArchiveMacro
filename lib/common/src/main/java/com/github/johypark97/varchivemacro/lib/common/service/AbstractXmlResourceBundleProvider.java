package com.github.johypark97.varchivemacro.lib.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;
import java.util.spi.ResourceBundleProvider;

public abstract class AbstractXmlResourceBundleProvider implements ResourceBundleProvider {
    protected static final String FORMAT_XML = "xml";

    protected String toBundleName(String baseName, Locale locale) {
        return getControl().toBundleName(baseName, locale);
    }

    protected String toResourceName(String bundleName) {
        return getControl().toResourceName(bundleName, FORMAT_XML);
    }

    private Control getControl() {
        return Control.getControl(Control.FORMAT_DEFAULT);
    }

    public static class XmlResourceBundle extends ResourceBundle {
        private final Map<String, Object> data = new HashMap<>();

        public XmlResourceBundle(InputStream stream) throws IOException {
            Properties properties = new Properties();
            properties.loadFromXML(stream);
            properties.forEach((key, value) -> data.put(key.toString(), value));
        }

        @Override
        protected Object handleGetObject(String key) {
            return data.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            Set<String> set = new HashSet<>(data.keySet());

            if (parent != null) {
                parent.getKeys().asIterator().forEachRemaining(set::add);
            }

            return new KeyEnumeration(set);
        }
    }


    public static class KeyEnumeration implements Enumeration<String> { // NOPMD
        private final Iterator<String> iterator;

        public KeyEnumeration(Set<String> set) {
            iterator = set.iterator();
        }

        @Override
        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        @Override
        public String nextElement() {
            return iterator.next();
        }
    }
}
