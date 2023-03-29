package com.github.johypark97.varchivemacro.macro.gui.model.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class ConfigData {
    @Expose
    public Path accountPath = Path.of("account.txt");

    @Expose
    public Path cacheDir = Path.of("cache");

    @Expose
    public Integer recordUploadDelay;

    @Expose
    public Integer scannerCaptureDelay;

    @Expose
    public Integer scannerKeyInputDuration;

    @Expose
    public Set<String> selectedDlcTabs = Set.of("PORTABLE 1", "PORTABLE 2");

    public void save(Path path) throws IOException {
        Files.writeString(path, createGson().toJson(this));
    }

    public static ConfigData load(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return createGson().fromJson(reader, ConfigData.class);
        }
    }

    private static Gson createGson() {
        GsonBuilder builder = newGsonBuilder_dump();

        JsonSerializer<Path> pathSerializer =
                (src, typeOfSrc, context) -> new JsonPrimitive(src.toString());
        JsonDeserializer<Path> pathDeserializer = (json, typeOfT, context) -> {
            try {
                return Path.of(json.getAsString());
            } catch (RuntimeException e) {
                throw new JsonParseException(e);
            }
        };

        builder.registerTypeAdapter(Path.class, pathSerializer);
        builder.registerTypeAdapter(Path.class, pathDeserializer);

        return builder.create();
    }
}
