package com.github.johypark97.varchivemacro.lib.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomGsonBuilder {
    public static Gson create() {
        GsonBuilder builder = new GsonBuilder();

        builder.disableHtmlEscaping();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.serializeNulls();
        builder.setPrettyPrinting();

        return builder.create();
    }
}
