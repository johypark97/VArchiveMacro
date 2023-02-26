package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DlcData {
    public record DlcInfo(@Expose String code, @Expose String name, @Expose String tab) {
    }


    @Expose
    public Map<Integer, DlcInfo> dlcs = new LinkedHashMap<>();

    @Expose
    public Map<Integer, List<Set<String>>> unlocks;

    public static DlcData loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, DlcData.class);
        }
    }
}
