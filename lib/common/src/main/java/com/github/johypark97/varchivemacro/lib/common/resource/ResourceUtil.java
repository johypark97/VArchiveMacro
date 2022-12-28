package com.github.johypark97.varchivemacro.lib.common.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

public class ResourceUtil {
    public static Properties loadProperties(URL url) throws IOException {
        if (url == null) {
            return null;
        }

        Properties properties = new Properties();
        try (InputStream stream = url.openStream()) {
            properties.load(stream);
        }

        return properties;
    }

    public static Properties loadXmlProperties(URL url) throws IOException {
        if (url == null) {
            return null;
        }

        Properties properties = new Properties();
        try (InputStream stream = url.openStream()) {
            properties.loadFromXML(stream);
        }

        return properties;
    }

    public static List<String> readAllLines(URL url, Charset encoding) throws IOException {
        if (url == null) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), encoding))) {
            return reader.lines().toList();
        }
    }

    public static List<String> readAllLines(URL url) throws IOException {
        return readAllLines(url, StandardCharsets.UTF_8);
    }
}
