package com.github.johypark97.varchivemacro.macro.fxgui.model;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultConfigModel implements ConfigModel {
    private static final Path CONFIG_PATH = Path.of("config.json");

    private final Gson gson;

    private ConfigData data = new ConfigData();

    public DefaultConfigModel() {
        JsonSerializer<Path> pathSerializer =
                (path, type, jsonSerializationContext) -> new JsonPrimitive(path.toString());

        JsonDeserializer<Path> pathDeserializer =
                (jsonElement, type, jsonDeserializationContext) -> {
                    try {
                        return Path.of(jsonElement.getAsString());
                    } catch (RuntimeException e) {
                        throw new JsonParseException(e);
                    }
                };

        gson = newGsonBuilder_dump().registerTypeAdapter(Path.class, pathSerializer)
                .registerTypeAdapter(Path.class, pathDeserializer).create();
    }

    @Override
    public boolean load() throws IOException {
        if (!Files.exists(CONFIG_PATH)) {
            return false;
        }

        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
            data = gson.fromJson(reader, ConfigData.class);
        }

        return true;
    }

    @Override
    public void save() throws IOException {
        Files.writeString(CONFIG_PATH, gson.toJson(data));
    }

    @Override
    public ScannerConfig getScannerConfig() {
        return data.scannerConfig;
    }

    @Override
    public void setScannerConfig(ScannerConfig value) {
        data.scannerConfig = value;
    }

    public static class ConfigData {
        @Expose
        public ScannerConfig scannerConfig = new ScannerConfig();
    }
}
