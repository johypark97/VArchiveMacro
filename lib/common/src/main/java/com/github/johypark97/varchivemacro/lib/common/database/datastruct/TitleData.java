package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

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

    public TitleData(List<ClippedData> clipped, List<RemapData> remap) {
        this.clipped = clipped;
        this.remap = remap;
    }

    public static TitleData loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, TitleData.class);
        }
    }

    public record ClippedData(@Expose int id, @Expose String value) {
    }


    public record RemapData(@Expose String to, @Expose List<String> from) {
    }
}
