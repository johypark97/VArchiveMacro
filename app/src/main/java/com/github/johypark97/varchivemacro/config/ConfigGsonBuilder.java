package com.github.johypark97.varchivemacro.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

class ConfigGsonBuilder {
    public static Gson create() {
        GsonBuilder builder = new GsonBuilder();

        builder.disableHtmlEscaping();
        builder.excludeFieldsWithoutExposeAnnotation();
        builder.serializeNulls();
        builder.setPrettyPrinting();

        return builder.create();
    }
}
