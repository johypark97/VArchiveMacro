package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public record Dlc(@Expose String name, @Expose int priority) {
    public static class GsonMapTypeToken extends TypeToken<Map<String, Dlc>> {
    }

    public static Map<String, Dlc> loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, new GsonMapTypeToken());
        }
    }
}