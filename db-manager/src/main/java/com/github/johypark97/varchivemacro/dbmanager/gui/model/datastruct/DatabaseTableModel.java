package com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import com.github.johypark97.varchivemacro.dbmanager.database.datastruct.Database;
import com.github.johypark97.varchivemacro.dbmanager.database.datastruct.Song;

public class DatabaseTableModel extends AbstractTableModel {
    private static final String ERROR_STRING = "ERROR";

    public static final List<String> COLUMNS = List.of("id", "title", "db_name", "composer", "dlc");

    private Database database;

    public DatabaseTableModel(Database database) {
        this.database = database;
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public int getRowCount() {
        return database.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0)
            return rowIndex;

        Song song = database.get(rowIndex);
        if (song == null)
            return ERROR_STRING;

        return switch (columnIndex) {
            case 1 -> song.title;
            case 2 -> song.db_name;
            case 3 -> song.composer;
            case 4 -> song.dlc;
            default -> ERROR_STRING;
        };
    }

    @Override
    public String getColumnName(int column) {
        String value = COLUMNS.get(column);
        return (value != null) ? value : ERROR_STRING;
    }
}
