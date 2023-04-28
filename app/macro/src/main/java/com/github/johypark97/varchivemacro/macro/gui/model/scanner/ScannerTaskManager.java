package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.Status;
import java.io.Serial;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;

class ScannerTaskManager {
    private final Map<Integer, ScannerTask> tasks = new ConcurrentHashMap<>();
    public final ScannerTaskTableModel tableModel = new ScannerTaskTableModel();

    private Path cacheDir = Path.of("");

    public void setCacheDir(Path path) {
        cacheDir = path;
    }

    public void clear() {
        tasks.clear();
        tableModel.fireTableDataChanged();
    }

    public ScannerTask create(LocalSong song, int songIndex, int songCount) {
        int taskNumber = tasks.size();
        ScannerTask task = new ScannerTask(this, taskNumber, song, songIndex, songCount, cacheDir);
        tasks.put(taskNumber, task);

        tableModel.fireTableRowsInserted(taskNumber, taskNumber);

        return task;
    }

    public ScannerTask getTask(int taskNumber) {
        return tasks.get(taskNumber);
    }

    public List<ScannerTask> getTasks() {
        return tasks.values().stream().toList();
    }

    public void notify_statusUpdated(int taskNumber) {
        tableModel.fireTableRowsUpdated(taskNumber, taskNumber);
    }

    protected class ScannerTaskTableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 2595265577036844112L;

        private static final List<String> COLUMNS =
                List.of("index", "count", "No", "Title", "Status");
        private static final String ERROR_STRING = "ERROR";

        private String statusToString(Status status) {
            return switch (status) {
                case ANALYZED -> "analyzed";
                case ANALYZING -> "analyzing";
                case CACHED -> "cached";
                case CAPTURED -> "captured";
                case DISK_LOADED -> "loaded from disk";
                case DISK_SAVED -> "saved to disk";
                case EXCEPTION -> "error occurred";
                case NONE -> "none";
                case WAITING -> "waiting";
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
            ScannerTask task = tasks.get(rowIndex);
            if (task == null) {
                return ERROR_STRING;
            }

            return switch (columnIndex) {
                case 0 -> task.songIndex;
                case 1 -> task.songCount;
                case 2 -> task.taskNumber;
                case 3 -> task.song.title();
                case 4 -> statusToString(task.getStatus());
                default -> ERROR_STRING;
            };
        }
    }
}
