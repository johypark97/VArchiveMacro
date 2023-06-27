package com.github.johypark97.varchivemacro.lib.common.resource;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.MissingResourceException;
import java.util.Properties;

public abstract class XmlResourceLoader {
    private static final String FORMAT_SUFFIX = ".xml";
    private static final String LOCALE_DELIMITER = "_";

    public final String path;

    protected Locale locale;

    public XmlResourceLoader(String path) {
        this.path = path;
    }

    public abstract Properties getProperty(String name) throws IOException;

    public Properties load() throws IOException {
        return load(Locale.getDefault());
    }

    public Properties load(Locale targetLocale) throws IOException {
        if (targetLocale == null) {
            throw new NullPointerException("locale is null");
        }

        for (Locale locale : resolveLocale(targetLocale)) {
            String name = toName(locale);
            Properties properties = getProperty(name);
            if (properties != null) {
                this.locale = locale;
                return properties;
            }
        }

        throw new MissingResourceException(
                String.format("Can't find XML resources for path %s, locale %s", path,
                        targetLocale), "", "");
    }

    public Locale getLocale() {
        return locale;
    }

    protected List<Locale> resolveLocale(Locale targetLocale) {
        List<Locale> list = new LinkedList<>();

        Builder builder = new Builder();
        list.add(builder.build());

        builder.setLanguage(targetLocale.getLanguage());
        list.add(0, builder.build());

        builder.setRegion(targetLocale.getCountry());
        list.add(0, builder.build());

        return list;
    }

    protected String toName(Locale locale) {
        StringBuilder builder = new StringBuilder();

        builder.append(path);
        String localeString = locale.toString();
        if (!localeString.isEmpty()) {
            builder.append(LOCALE_DELIMITER).append(localeString);
        }
        builder.append(FORMAT_SUFFIX);

        return builder.toString();
    }
}
