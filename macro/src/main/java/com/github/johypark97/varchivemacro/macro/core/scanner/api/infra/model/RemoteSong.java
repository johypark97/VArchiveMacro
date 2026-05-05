package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model;

import static com.github.johypark97.varchivemacro.macro.common.GsonWrapper.newGsonBuilder_general;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class RemoteSong {
    @Expose
    @SerializedName("title")
    public int id;

    @Expose
    @SerializedName("name")
    public String title;

    @Expose
    public String composer;

    @Expose
    public String dlcCode;

    @Expose
    public String dlc;

    public static List<RemoteSong> fromJson(String json) {
        Gson gson = newGsonBuilder_general().create();
        return gson.fromJson(json, new GsonListTypeToken());
    }

    public static class GsonListTypeToken extends TypeToken<List<RemoteSong>> {
    }
}
