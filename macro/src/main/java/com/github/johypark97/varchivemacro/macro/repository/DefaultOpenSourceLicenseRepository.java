package com.github.johypark97.varchivemacro.macro.repository;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;

import com.github.johypark97.varchivemacro.macro.resource.License;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DefaultOpenSourceLicenseRepository implements OpenSourceLicenseRepository {
    private static final String BASE_PATH = "/licenses/";
    private static final String JSON_PATH = BASE_PATH + "licenses.json";

    private final Map<String, License> libraryLicenseMap;

    public DefaultOpenSourceLicenseRepository() {
        URL url = getClass().getResource(JSON_PATH);
        if (url == null) {
            throw new RuntimeException("file not found: " + JSON_PATH);
        }

        try (InputStream stream = url.openStream()) {
            List<String> lines = readAllLines(stream);
            String allLine = String.join("", lines);

            Gson gson = newGsonBuilder_general().create();
            libraryLicenseMap = gson.fromJson(allLine, new License.GsonTypeToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getLibraryList() {
        return libraryLicenseMap.keySet().stream().toList();
    }

    @Override
    public String getLicenseText(String library) throws IOException {
        License license = libraryLicenseMap.get(library);
        if (license == null) {
            return "ERROR: Unknown library";
        }

        URL url = getClass().getResource(BASE_PATH + license.path());
        if (url == null) {
            return "ERROR: resource not found";
        }

        List<String> lineList;
        try (InputStream stream = url.openStream()) {
            lineList = readAllLines(stream);
        }

        return String.join(System.lineSeparator(), lineList);
    }

    @Override
    public String getLibraryUrl(String library) {
        License license = libraryLicenseMap.get(library);
        if (license == null) {
            return "";
        }

        return license.url();
    }
}
