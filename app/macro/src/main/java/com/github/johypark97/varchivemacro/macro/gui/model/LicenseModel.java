package com.github.johypark97.varchivemacro.macro.gui.model;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LicenseModel {
    private static final String NEWLINE = System.lineSeparator();
    private static final String PATH = "/licenses/";

    private static final Map<String, String> LICNESES =
            Map.ofEntries(Map.entry("Gson", PATH + "gson.txt"),
                    Map.entry("JNativeHook", PATH + "jnativehook.txt"),
                    Map.entry("Launch4j", PATH + "launch4j.txt"));

    public String[] getList() {
        return LICNESES.keySet().stream().sorted().toArray(String[]::new);
    }

    public String getText(String key) throws IOException {
        String path = LICNESES.get(key);
        if (path == null)
            return "ERROR: invalid key";

        List<String> lines = readAllLines(getClass().getResource(path));
        if (lines == null)
            return "ERROR: resource not found";

        return lines.stream().collect(Collectors.joining(NEWLINE));
    }
}
