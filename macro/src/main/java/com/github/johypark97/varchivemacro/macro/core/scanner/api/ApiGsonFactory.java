package com.github.johypark97.varchivemacro.macro.core.scanner.api;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Button;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiGsonFactory {
    public static Gson create() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Button.class, new Button.GsonSerializer())
                .registerTypeAdapter(Button.class, new Button.GsonDeserializer())
                .create();
    }
}
