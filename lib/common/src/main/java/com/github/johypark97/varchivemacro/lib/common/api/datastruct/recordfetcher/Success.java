package com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher;

import com.google.gson.annotations.Expose;
import java.util.List;

public class Success {
    @Expose
    public boolean success; // NOPMD

    @Expose
    public String board;

    @Expose
    public int button;

    @Expose
    public int totalCount;

    @Expose
    public List<Floor> floors;
}
