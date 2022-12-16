package com.github.johypark97.varchivemacro.dbmanager.database.datastruct;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Song {
    @Expose
    public String title;

    @Expose
    public String remote_title;

    @Expose
    public String composer;

    @Expose
    public String dlc;

    @Expose
    public List<Score> scores = new ArrayList<>();
}
