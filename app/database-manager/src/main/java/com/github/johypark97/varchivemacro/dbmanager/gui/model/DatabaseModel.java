package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.dbmanager.database.datastruct.Database;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableRowSorter;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DatabaseModel {
    private Database database;

    private DatabaseTableModel tableModel;
    private DatabaseTableRowSorter tableRowSorter;

    public void loadFile(Path path) throws JsonSyntaxException, JsonIOException, IOException {
        database = Database.loadJson(path);

        tableModel = new DatabaseTableModel(database);
        tableRowSorter = new DatabaseTableRowSorter(tableModel);
    }

    public TableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<TableModel> getTableRowSorter() {
        return tableRowSorter;
    }

    public String[] getFilterableColumns() {
        return DatabaseTableModel.COLUMNS.stream().filter((x) -> !x.equals("id"))
                .toArray(String[]::new);
    }

    public void setFilter(String pattern, String column) {
        if (tableRowSorter != null) {
            tableRowSorter.setFilter(pattern, column);
        }
    }
}
