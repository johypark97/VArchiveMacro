package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model.ButtonJson;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RecordGsonFactory {
    public static Gson create() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(ButtonJson.class, new ButtonJson.GsonSerializer())
                .registerTypeAdapter(ButtonJson.class, new ButtonJson.GsonDeserializer())
                .create();
    }
}
