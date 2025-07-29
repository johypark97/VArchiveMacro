package com.github.johypark97.varchivemacro.macro.common.license.infra.service;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;

import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import com.github.johypark97.varchivemacro.macro.common.license.infra.model.LicenseJson;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OpenSourceLicenseLoadService {
    private static final String BASE_PATH = "/licenses/";
    private static final String JSON_PATH = BASE_PATH + "licenses.json";

    public List<License> load() throws IOException {
        URL jsonUrl = getClass().getResource(JSON_PATH);
        if (jsonUrl == null) {
            throw new FileNotFoundException("LicenseJson not found: " + JSON_PATH);
        }

        Map<String, LicenseJson> jsonMap;
        try (InputStream stream = jsonUrl.openStream()) {
            List<String> lineList = readAllLines(stream);
            String allLine = String.join("", lineList);

            Gson gson = newGsonBuilder_general().create();
            jsonMap = gson.fromJson(allLine, new LicenseJson.GsonTypeToken());
        }

        List<License> licenseList = new LinkedList<>();
        for (Map.Entry<String, LicenseJson> entry : jsonMap.entrySet()) {
            URL libraryUrl = getClass().getResource(BASE_PATH + entry.getValue().path());
            if (libraryUrl == null) {
                throw new FileNotFoundException(
                        "License file not found: " + entry.getValue().path());
            }

            String licenseText;
            try (InputStream stream = libraryUrl.openStream()) {
                List<String> lineList = readAllLines(stream);
                licenseText = String.join(System.lineSeparator(), lineList);
            }

            licenseList.add(new License(entry.getKey(), entry.getValue().owner(), licenseText,
                    entry.getValue().url()));
        }

        return licenseList;
    }
}
