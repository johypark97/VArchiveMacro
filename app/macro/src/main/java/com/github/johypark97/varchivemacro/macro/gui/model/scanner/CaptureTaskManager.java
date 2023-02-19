package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CaptureTask.Status;
import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;

class CaptureTaskManager {
    private final Map<Integer, CaptureTask> tasks = new ConcurrentHashMap<>();
    public final ScannerTaskTableModel tableModel = new ScannerTaskTableModel();

    public void clear() {
        tasks.clear();
        tableModel.fireTableDataChanged();
    }

    public CaptureTask create(LocalSong song) {
        int taskNumber = tasks.size();

        CaptureTask task = new CaptureTask(this, taskNumber, song);
        tasks.put(taskNumber, task);

        tableModel.fireTableRowsInserted(taskNumber, taskNumber);

        return task;
    }

    public CaptureTask getTask(int taskNumber) {
        return tasks.get(taskNumber);
    }

    public void notify_statusUpdated(int taskNumber) {
        tableModel.fireTableRowsUpdated(taskNumber, taskNumber);
    }

    protected class ScannerTaskTableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 2595265577036844112L;

        private static final List<String> COLUMNS = List.of("No", "Title", "Status");
        private static final String ERROR_STRING = "ERROR";

        private String statusToString(Status status) {
            return switch (status) {
                case CACHED -> "cached";
                case CAPTURED -> "captured";
                case DISK_LOADED -> "loaded from disk";
                case DISK_SAVED -> "saved to disk";
                case EXCEPTION -> "exception occurred";
                case NONE -> "none";
            };
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS.get(column);
        }

        @Override
        public int getRowCount() {
            return tasks.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CaptureTask task = tasks.get(rowIndex);
            if (task == null) {
                return ERROR_STRING;
            }

            return switch (columnIndex) {
                case 0 -> task.taskNumber;
                case 1 -> task.getSongTitle();
                case 2 -> statusToString(task.getStatus());
                default -> ERROR_STRING;
            };
        }
    }
}
