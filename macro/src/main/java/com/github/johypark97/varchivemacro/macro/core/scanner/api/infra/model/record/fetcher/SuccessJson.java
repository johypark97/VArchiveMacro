package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.fetcher;

import static com.github.johypark97.varchivemacro.macro.common.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.macro.libscanner.Enums;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SuccessJson {
    @Expose
    public boolean success;

    @Expose
    @SerializedName("nickname")
    public String djName;

    @Expose
    public Enums.Button button;

    @Expose
    public int count;

    @Expose
    public List<RecordJson> records;

    public static SuccessJson fromJson(String json) {
        Gson gson = newGsonBuilder_general().create();
        return gson.fromJson(json, SuccessJson.class);
    }
}
