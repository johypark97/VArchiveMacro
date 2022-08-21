package com.github.johypark97.varchivemacro.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class LicenseModel {
    private static final Map<String, String> licneses;
    private static final String NEWLINE = System.lineSeparator();
    private static final String PATH = "/licenses/";

    static {
        licneses = new HashMap<>();
        licneses.put("Shadow", PATH + "shadow.txt");
        licneses.put("Gson", PATH + "gson.txt");
        licneses.put("JNativeHook", PATH + "jnativehook.txt");
        licneses.put("Launch4j", PATH + "launch4j.txt");
    }

    public String[] getList() {
        return licneses.keySet().stream().sorted().toArray(String[]::new);
    }

    public String getText(String key) throws IOException {
        String path = licneses.get(key);
        if (path == null)
            return "ERROR: invalid key";

        URL url = getClass().getResource(path);
        if (url == null)
            return "ERROR: resource not found";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.lines().collect(Collectors.joining(NEWLINE));
        }
    }
}
