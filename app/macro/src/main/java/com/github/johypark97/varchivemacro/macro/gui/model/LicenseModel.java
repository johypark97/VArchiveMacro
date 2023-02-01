package com.github.johypark97.varchivemacro.macro.gui.model;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class LicenseModel {
    private static final String NEWLINE = System.lineSeparator();
    private static final String PATH = "/licenses/";

    private static final Map<String, String> LICENSES =
            Map.ofEntries(Map.entry("Gson", PATH + "gson.txt"),
                    Map.entry("JNativeHook", PATH + "jnativehook.txt"),
                    Map.entry("Launch4j", PATH + "launch4j.txt"));

    public List<String> getList() {
        return LICENSES.keySet().stream().sorted().toList();
    }

    public String getText(String key) throws IOException {
        String path = LICENSES.get(key);
        if (path == null) {
            return "ERROR: invalid key";
        }

        URL url = getClass().getResource(path);
        if (url == null) {
            return "ERROR: resource not found";
        }

        List<String> lines;
        try (InputStream stream = url.openStream()) {
            lines = readAllLines(stream);
        }

        return String.join(NEWLINE, lines);
    }
}
