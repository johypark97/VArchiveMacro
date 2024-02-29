package com.github.johypark97.varchivemacro.lib.scanner.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongManager.LocalSong;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record SongData(@Expose int id, @Expose String title, @Expose String remote_title,
                       @Expose String composer, @Expose String dlcCode, @Expose int priority) {
    public static List<SongData> loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, new GsonListTypeToken());
        }
    }

    public LocalSong toLocalSong() {
        return new LocalSong(id, title, remote_title, composer, dlcCode, priority);
    }

    public static class GsonListTypeToken extends TypeToken<List<SongData>> {
    }
}
