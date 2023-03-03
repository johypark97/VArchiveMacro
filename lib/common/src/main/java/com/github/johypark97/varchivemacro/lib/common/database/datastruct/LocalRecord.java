package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LocalRecord {
    public static class GsonListTypeToken extends TypeToken<List<LocalRecord>> {
    }


    @Expose
    public final int id;

    @Expose
    public final Button button;

    @Expose
    public final Pattern pattern;

    @Expose
    public float score;

    @Expose
    public int maxCombo;

    public LocalRecord(int id, Button button, Pattern pattern, float score, int maxCombo) {
        if (score < 0.0f || score > 100.0f) {
            throw new IllegalArgumentException("invalid score: " + score);
        }

        this.button = button;
        this.id = id;
        this.maxCombo = (score == 100.0f || maxCombo != 0) ? 1 : 0;
        this.pattern = pattern;
        this.score = score;
    }

    public boolean isUpdated(LocalRecord record) {
        if (id != record.id || button != record.button || pattern != record.pattern) {
            String format = "%d. %sB %s";
            String a = String.format(format, id, button, pattern);
            String b = String.format(format, record.id, record.button, record.pattern);
            String message = String.format("is a different song: %s <-> %s", a, b);
            throw new IllegalArgumentException(message);
        }

        return record.score > score || record.maxCombo > maxCombo;
    }

    public boolean update(LocalRecord record) {
        if (!isUpdated(record)) {
            return false;
        }

        score = Math.max(score, record.score);
        maxCombo = Math.max(maxCombo, record.maxCombo);
        return true;
    }

    // Temporary method to resolve spotbugs URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD warning.
    @Override
    public String toString() {
        return String.format("%d. %sB %s %.2f(%d)", id, button, pattern, score, maxCombo);
    }

    public static List<LocalRecord> loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, new GsonListTypeToken());
        }
    }

    public static void saveJson(Path path, List<LocalRecord> records) throws IOException {
        GsonBuilder builder = newGsonBuilder_dump();
        Gson gson = builder.registerTypeAdapter(Button.class, new Button.GsonSerializer()).create();
        Files.writeString(path, gson.toJson(records));
    }
}
