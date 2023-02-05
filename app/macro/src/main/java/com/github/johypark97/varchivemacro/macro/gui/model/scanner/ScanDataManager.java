package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;

public class ScanDataManager {
    private final Map<Integer, ScanData> dataList = new ConcurrentHashMap<>();

    public final ScannerTaskTableModel tableModel = new ScannerTaskTableModel();

    public void clear() {
        dataList.clear();
        tableModel.fireTableDataChanged();
    }

    public ScanData add(LocalSong song) {
        int taskNumber = dataList.size();

        ScanData data = new ScanData(this, taskNumber, song);
        dataList.put(taskNumber, data);

        tableModel.fireTableRowsInserted(taskNumber, taskNumber);

        return data;
    }

    public void notify_statusUpdated(int taskNumber) {
        tableModel.fireTableRowsUpdated(taskNumber, taskNumber);
    }

    protected class ScannerTaskTableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 2595265577036844112L;

        private static final List<String> COLUMNS = List.of("No", "Title", "Status");
        private static final String ERROR_STRING = "ERROR";

        @Override
        public String getColumnName(int column) {
            return COLUMNS.get(column);
        }

        @Override
        public int getRowCount() {
            return dataList.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ScanData data = dataList.get(rowIndex);
            if (data == null) {
                return ERROR_STRING;
            }

            return switch (columnIndex) {
                case 0 -> data.taskNumber;
                case 1 -> data.getTitle();
                case 2 -> data.getStatus();
                default -> ERROR_STRING;
            };
        }
    }
}
