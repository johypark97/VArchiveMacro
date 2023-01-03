package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableRowSorter;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.Database;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;

public class DatabaseModel {
    public final Database database;

    public final DatabaseTableModel tableModel;
    public final DatabaseTableRowSorter tableRowSorter;

    public DatabaseModel(Path path) throws JsonSyntaxException, JsonIOException, IOException {
        database = Database.loadJson(path);

        tableModel = new DatabaseTableModel(this);
        tableRowSorter = new DatabaseTableRowSorter(tableModel);
    }

    public String[] getFilterableColumns() {
        return DatabaseTableModel.COLUMNS.stream().filter((x) -> !"id".equals(x))
                .toArray(String[]::new);
    }

    public void setFilter(String pattern, String column) {
        if (tableRowSorter != null) {
            tableRowSorter.setFilter(pattern, column);
        }
    }
}
