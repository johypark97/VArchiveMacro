package com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher;

import com.google.gson.annotations.Expose;
import java.util.List;

public class Floor {
    @Expose
    public float floorNumber;

    @Expose
    public List<Pattern> patterns;
}
