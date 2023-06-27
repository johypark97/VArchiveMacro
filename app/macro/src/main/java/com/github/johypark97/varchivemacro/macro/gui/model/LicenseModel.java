package com.github.johypark97.varchivemacro.macro.gui.model;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;

import com.github.johypark97.varchivemacro.macro.gui.model.datastruct.LicenseData;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class LicenseModel {
    private static final String BASE_PATH = "/licenses/";
    private static final String JSON_PATH = BASE_PATH + "licenses.json";

    private final Map<String, LicenseData> licenses;

    public LicenseModel() {
        URL url = getClass().getResource(JSON_PATH);
        if (url == null) {
            throw new RuntimeException("file not found: " + JSON_PATH);
        }

        try (InputStream stream = url.openStream()) {
            List<String> lines = readAllLines(stream);
            String allLine = String.join("", lines);

            Gson gson = newGsonBuilder_general().create();
            licenses = gson.fromJson(allLine, new LicenseData.GsonTypeToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getList() {
        return licenses.keySet().stream().sorted().toList();
    }

    public String getText(String key) throws IOException {
        LicenseData data = licenses.get(key);

        URL url = getClass().getResource(BASE_PATH + data.path());
        if (url == null) {
            return "ERROR: resource not found";
        }

        List<String> lines;
        try (InputStream stream = url.openStream()) {
            lines = readAllLines(stream);
        }

        return String.join(System.lineSeparator(), lines);
    }

    public String getUrl(String key) {
        LicenseData data = licenses.get(key);
        return data.url();
    }
}
