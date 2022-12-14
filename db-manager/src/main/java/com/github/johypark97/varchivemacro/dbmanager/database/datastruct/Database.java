package com.github.johypark97.varchivemacro.dbmanager.database.datastruct;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import com.github.johypark97.varchivemacro.lib.json.CustomGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Database extends HashMap<Integer, Song> {
    public static Database loadJson(Path path)
            throws JsonSyntaxException, JsonIOException, IOException {
        Gson gson = CustomGsonBuilder.create();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return gson.fromJson(reader, Database.class);
        }
    }
}
