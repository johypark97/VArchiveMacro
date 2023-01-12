package com.github.johypark97.varchivemacro.lib.common.api.datastruct.staticfetcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class RemoteSong {
    public static class GsonListTypeToken extends TypeToken<List<RemoteSong>> {
    }


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
}
