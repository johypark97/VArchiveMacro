package com.github.johypark97.varchivemacro.macro.common.programdata.infra.model;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public record DataVersionJson(@Expose long version,
                              @Expose @SerializedName("files") List<DataFile> fileList) {
    public static DataVersionJson from(DataVersion dataVersion) {
        return new DataVersionJson(dataVersion.version(),
                dataVersion.fileList().stream().map(DataFile::from).toList());
    }

    public static DataVersionJson from(Path path) throws IOException {
        Gson gson = GsonWrapper.newGsonBuilder_general().create();

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, DataVersionJson.class);
        }
    }

    public static DataVersionJson from(String data) {
        Gson gson = GsonWrapper.newGsonBuilder_general().create();

        return gson.fromJson(data, DataVersionJson.class);
    }

    public DataVersion toDomain() {
        return new DataVersion(version, fileList.stream().map(DataFile::toDomain).toList());
    }

    public void write(Path path) throws IOException {
        Gson gson = GsonWrapper.newGsonBuilder_dump().create();

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(gson.toJson(this));
            writer.write('\n');
        }
    }

    public record DataFile(@Expose String path,
                           @Expose @SerializedName("request_path") String requestPath,
                           @Expose String hash) {
        public static DataFile from(DataVersion.DataFile domain) {
            return new DataFile(domain.path(), domain.requestPath(), domain.hash());
        }

        public DataVersion.DataFile toDomain() {
            return new DataVersion.DataFile(path, requestPath, hash);
        }
    }
}
