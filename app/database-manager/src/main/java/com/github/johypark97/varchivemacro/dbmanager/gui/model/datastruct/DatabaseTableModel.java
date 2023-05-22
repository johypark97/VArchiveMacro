package com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct;

import com.github.johypark97.varchivemacro.lib.common.database.ISongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.Serial;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DatabaseTableModel extends AbstractTableModel {
    @Serial
    private static final long serialVersionUID = 8488003171979670883L;

    private static final String ERROR_STRING = "ERROR";

    public static final List<String> COLUMNS =
            List.of("no", "id", "title", "remote_title", "composer", "dlc", "dlcCode", "dlcTab",
                    "priority");

    private final List<LocalSong> songs;

    public DatabaseTableModel(ISongManager songManager) {
        songs = songManager.getSongList();
    }

    @Override
    public int getRowCount() {
        return songs.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LocalSong localSong = songs.get(rowIndex);
        if (localSong == null) {
            return ERROR_STRING;
        }

        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> localSong.id();
            case 2 -> localSong.title();
            case 3 -> localSong.remote_title();
            case 4 -> localSong.composer();
            case 5 -> localSong.dlc();
            case 6 -> localSong.dlcCode();
            case 7 -> localSong.dlcTab();
            case 8 -> localSong.priority();
            default -> ERROR_STRING;
        };
    }

    @Override
    public String getColumnName(int column) {
        String value = COLUMNS.get(column);
        return (value != null) ? value : ERROR_STRING;
    }
}
