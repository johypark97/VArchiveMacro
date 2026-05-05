package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.uploader;

import static com.github.johypark97.varchivemacro.macro.common.GsonWrapper.newGsonBuilder_general;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class SuccessJson {
    @Expose
    public boolean success;

    @Expose
    public boolean update;

    public static SuccessJson fromJson(String json) {
        Gson gson = newGsonBuilder_general().create();
        return gson.fromJson(json, SuccessJson.class);
    }
}
