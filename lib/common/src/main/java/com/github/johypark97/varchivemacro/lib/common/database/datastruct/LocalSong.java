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

public record LocalSong(@Expose int id, @Expose String title, @Expose String remote_title,
                        @Expose String composer, @Expose String dlc, @Expose String dlcCode) {
    public static class GsonMapTypeToken extends TypeToken<Map<Integer, LocalSong>> {
    }

    public static Map<Integer, LocalSong> loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, new GsonMapTypeToken());
        }
    }
}