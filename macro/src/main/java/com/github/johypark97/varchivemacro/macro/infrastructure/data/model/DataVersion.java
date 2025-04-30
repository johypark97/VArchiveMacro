package com.github.johypark97.varchivemacro.macro.infrastructure.data.model;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record DataVersion(@Expose long version,
                          @Expose @SerializedName("files") List<DataFile> fileList) {
    public static DataVersion from(Path path) throws IOException {
        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, DataVersion.class);
        }
    }

    public static DataVersion from(String data) {
        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        return gson.fromJson(data, DataVersion.class);
    }

    public void write(Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            Gson gson = GsonWrapper.newGsonBuilder_dump().create();
            writer.write(gson.toJson(this));
            writer.write('\n');
        }
    }

    public record DataFile(@Expose String path,
                           @Expose @SerializedName("request_path") String requestPath,
                           @Expose String hash) {
    }
}
