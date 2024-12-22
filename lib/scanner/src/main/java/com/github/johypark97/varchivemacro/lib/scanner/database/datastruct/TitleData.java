package com.github.johypark97.varchivemacro.lib.scanner.database.datastruct;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_dump;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TitleData {
    @Expose
    public final List<ClippedData> clipped;

    @Expose
    public final List<RemapData> remap;

    @Expose
    public final List<RemoteTitleData> remoteTitle;

    public TitleData(List<ClippedData> clipped, List<RemapData> remap,
            List<RemoteTitleData> remoteTitle) {
        this.clipped = clipped;
        this.remap = remap;
        this.remoteTitle = remoteTitle;
    }

    public static TitleData loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, TitleData.class);
        }
    }

    public record ClippedData(@Expose int id, @Expose String value) {
    }


    public static class RemapData {
        @Expose
        public String to;

        @Expose
        public List<String> from;
    }


    public record RemoteTitleData(@Expose int id, @Expose String value) {
    }
}
