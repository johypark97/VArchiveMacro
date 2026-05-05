package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.fetcher;

import com.github.johypark97.varchivemacro.macro.libscanner.Enums;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordJson {
    @Expose
    @SerializedName("title")
    public int id;

    @Expose
    public String name;

    @Expose
    public String dlcCode;

    @Expose
    public Enums.Pattern pattern;

    @Expose
    public int level;

    /**
     * nullable
     */
    @Expose
    public Integer floor;

    /**
     * nullable
     */
    @Expose
    public String floorName;

    @Expose
    public boolean newTab;

    @Expose
    public int maxRating;

    @Expose
    public float score;

    @Expose
    public boolean maxCombo;

    /**
     * nullable
     */
    @Expose
    public Double rating;

    @Expose
    public double djpower;

    /**
     * nullable
     */
    @Expose
    public Double maxDjpower;

    /**
     * nullable
     */
    @Expose
    public String updatedAt;
}
