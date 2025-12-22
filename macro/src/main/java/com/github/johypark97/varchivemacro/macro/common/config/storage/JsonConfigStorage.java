package com.github.johypark97.varchivemacro.macro.common.config.storage;

import com.github.johypark97.varchivemacro.macro.common.config.model.AppConfig;
import com.github.johypark97.varchivemacro.macro.common.config.storage.dto.AppConfigDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class JsonConfigStorage implements ConfigStorage<AppConfig> {
    private final Gson gson =
            new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping()
                    .serializeNulls().setPrettyPrinting().create();

    private final Path configFilePath;

    public JsonConfigStorage(Path configFilePath) {
        this.configFilePath = Objects.requireNonNull(configFilePath);
    }

    @Override
    public AppConfig read() throws IOException {
        AppConfigDto dto;

        try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
            dto = gson.fromJson(reader, AppConfigDto.class);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }

        if (dto == null) {
            throw new IOException("Failed to read config: JSON is invalid or empty.");
        }

        return dto.toModel();
    }

    @Override
    public void write(AppConfig config) throws IOException {
        AppConfigDto dto = AppConfigDto.fromModel(config);

        try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
            gson.toJson(dto, writer);
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
    }
}
