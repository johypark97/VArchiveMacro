package com.github.johypark97.varchivemacro.dbmanager.database.datastruct;

import com.github.johypark97.varchivemacro.lib.common.json.CustomGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Database extends HashMap<Integer, Song> {
    @Serial
    private static final long serialVersionUID = 7637060877206612275L;

    public static Database loadJson(Path path)
            throws JsonSyntaxException, JsonIOException, IOException {
        Gson gson = CustomGsonBuilder.create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, Database.class);
        }
    }
}
