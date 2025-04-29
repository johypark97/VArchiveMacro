package com.github.johypark97.varchivemacro.macro.infrastructure.license.repository;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;

import com.github.johypark97.varchivemacro.macro.infrastructure.license.model.LibraryLicense;
import com.github.johypark97.varchivemacro.macro.infrastructure.license.model.License;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class DefaultOpenSourceLicenseRepository implements OpenSourceLicenseRepository {
    private static final String BASE_PATH = "/licenses/";
    private static final String JSON_PATH = BASE_PATH + "licenses.json";

    private Map<String, LibraryLicense> libraryLicenseMap;

    @Override
    public void load() {
        URL url = getClass().getResource(BASE_PATH + JSON_PATH);
        if (url == null) {
            throw new RuntimeException(new FileNotFoundException(
                    "LibraryLicense not found: " + BASE_PATH + JSON_PATH));
        }

        try (InputStream stream = url.openStream()) {
            List<String> lineList = readAllLines(stream);
            String allLine = String.join("", lineList);

            Gson gson = newGsonBuilder_general().create();
            libraryLicenseMap = gson.fromJson(allLine, new LibraryLicense.GsonTypeToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findAllLibrary() {
        return libraryLicenseMap.keySet().stream().toList();
    }

    @Override
    public License findLicense(String library) throws IOException {
        if (libraryLicenseMap == null) {
            throw new IllegalStateException("Not loaded");
        }

        LibraryLicense libraryLicense = libraryLicenseMap.get(library);
        if (libraryLicense == null) {
            throw new RuntimeException("Unknown library.");
        }

        URL url = getClass().getResource(BASE_PATH + libraryLicense.path());
        if (url == null) {
            throw new FileNotFoundException("License file not found: " + libraryLicense.path());
        }

        String license;
        try (InputStream stream = url.openStream()) {
            List<String> lineList = readAllLines(stream);
            license = String.join(System.lineSeparator(), lineList);
        }

        return new License(license, libraryLicense.url());
    }
}
