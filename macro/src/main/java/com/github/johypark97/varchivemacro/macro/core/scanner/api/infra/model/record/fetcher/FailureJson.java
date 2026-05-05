package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.fetcher;

import static com.github.johypark97.varchivemacro.macro.common.GsonWrapper.newGsonBuilder_general;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

public class FailureJson {
    /**
     * nullable
     */
    @Expose
    public Boolean success;

    /**
     * nullable
     */
    @Expose
    public Integer errorCode;

    @Expose
    public String message;

    public static FailureJson fromJson(String json) {
        Gson gson = newGsonBuilder_general().create();
        return gson.fromJson(json, FailureJson.class);
    }
}
