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
    public final String titleChars;

    @Expose
    public final List<ShortTitle> shortTitles;

    public TitleData(String titleChars, List<ShortTitle> shortTitles) {
        this.shortTitles = shortTitles;
        this.titleChars = titleChars;
    }

    public static TitleData loadJson(Path path) throws IOException {
        Gson gson = newGsonBuilder_dump().create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, TitleData.class);
        }
    }

    public record ShortTitle(@Expose int id, @Expose String value) {
    }
}
