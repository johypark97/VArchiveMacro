package com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct;

import com.github.johypark97.varchivemacro.dbmanager.database.datastruct.Database;
import com.github.johypark97.varchivemacro.dbmanager.database.datastruct.Song;
import java.io.Serial;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 8488003171979670883L;

    private static final String ERROR_STRING = "ERROR";

    public static final List<String> COLUMNS =
            List.of("id", "title", "remote_title", "composer", "dlc");

    private final Database database;

    public DatabaseTableModel(Database database) {
        this.database = database;
    }

    @Override
    public int getRowCount() {
        return database.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex;
        }

        Song song = database.get(rowIndex);
        if (song == null) {
            return ERROR_STRING;
        }

        return switch (columnIndex) {
            case 1 -> song.title;
            case 2 -> song.remote_title;
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
