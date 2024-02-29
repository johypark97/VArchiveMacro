package com.github.johypark97.varchivemacro.lib.scanner.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record RecordData(@Expose int id, @Expose Button button, @Expose Pattern pattern,
                         @Expose float rate, @Expose boolean maxCombo) {
    public static List<RecordData> loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, new GsonListTypeToken());
        }
    }

    public static void saveJson(Path path, List<RecordData> records) throws IOException {
        GsonBuilder builder = newGsonBuilder_dump();
        Gson gson = builder.registerTypeAdapter(Button.class, new Button.GsonSerializer()).create();
        Files.writeString(path, gson.toJson(records));
    }

    public static RecordData fromLocalRecord(LocalRecord record) {
        return new RecordData(record.id, record.button, record.pattern, record.rate,
                record.maxCombo);
    }

    public LocalRecord toLocalRecord() {
        return new LocalRecord(id, button, pattern, rate, maxCombo);
    }

    public static class GsonListTypeToken extends TypeToken<List<RecordData>> {
    }
}
