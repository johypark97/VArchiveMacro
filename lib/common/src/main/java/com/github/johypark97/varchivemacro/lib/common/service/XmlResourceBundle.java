package com.github.johypark97.varchivemacro.lib.common.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

public class XmlResourceBundle extends ResourceBundle {
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
