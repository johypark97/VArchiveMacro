package com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.Serial;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 8488003171979670883L;

    private static final String ERROR_STRING = "ERROR";

    public static final List<String> COLUMNS =
            List.of("id", "title", "remote_title", "composer", "dlc", "dlcCode");

    // model
    public final DatabaseModel model;

    public DatabaseTableModel(DatabaseModel model) {
        this.model = model;
    }

    @Override
    public int getRowCount() {
        return model.songManager.songCount();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LocalSong localSong = model.songManager.getSong(rowIndex);
        if (localSong == null) {
            return ERROR_STRING;
        }

        return switch (columnIndex) {
            case 0 -> localSong.id();
            case 1 -> localSong.title();
            case 2 -> localSong.remote_title();
            case 3 -> localSong.composer();
            case 4 -> localSong.dlc();
            case 5 -> localSong.dlcCode();
            default -> ERROR_STRING;
        };
    }

    @Override
    public String getColumnName(int column) {
        String value = COLUMNS.get(column);
        return (value != null) ? value : ERROR_STRING;
    }
}
