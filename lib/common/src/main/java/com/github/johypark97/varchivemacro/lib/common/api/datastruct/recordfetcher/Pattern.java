package com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pattern {
    @Expose
    @SerializedName("title")
    public int id;

    @Expose
    @SerializedName("name")
    public String title;

    @Expose
    public String composer;

    @Expose
    public String pattern; // NOPMD

    @Expose
    public float score;

    @Expose
    public int maxCombo;

    @Expose
    public String dlc;

    @Expose
    public String dlcCode;
}
