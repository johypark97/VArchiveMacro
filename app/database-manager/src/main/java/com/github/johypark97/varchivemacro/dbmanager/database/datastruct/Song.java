package com.github.johypark97.varchivemacro.dbmanager.database.datastruct;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;

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
    public List<Record> records = new ArrayList<>();

    @Override
    public String toString() {
        return String.format("%s - %s (%s) { %s }", title, composer, dlc,
                String.join(", ", records.stream().map(Record::toString).toList()));
    }
}
