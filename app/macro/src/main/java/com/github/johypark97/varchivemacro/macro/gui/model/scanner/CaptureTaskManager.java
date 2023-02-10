package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CaptureTask.Status;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.table.AbstractTableModel;

class CaptureTaskManager {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.dir"), "image");
    private static final String FORMAT = "png";

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

    public void saveToDisk() throws IOException {
        Path dirPath = getFilePath(0).getParent();
        if (dirPath != null) {
            Files.createDirectories(dirPath);
        }

        for (CaptureTask task : tasks.values()) {
            byte[] bytes = task.getImageBytes();

            Path path = getFilePath(task.getSongId());
            Files.deleteIfExists(path);
            Files.write(path, bytes);

            task.setStatus(Status.DISK_SAVED);
        }
    }

    public void loadFromDisk() throws IOException {
        for (CaptureTask task : tasks.values()) {
            Path path = getFilePath(task.getSongId());
            if (Files.exists(path)) {
                task.setImageBytes(Files.readAllBytes(path));
                task.setStatus(Status.DISK_LOADED);
            }
        }
    }

    private Path getFilePath(int id) {
        return BASE_PATH.resolve(String.format("%04d.%s", id, FORMAT));
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
