package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.util.TitleComparator;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ResultManager.RecordData.Result;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.AnalyzedData;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ResultManager {
    public static class RecordData {
        public enum Result {
            CANCELED, HIGHER_RECORD_EXISTS, NOT_UPLOADED, SUSPENDED, UPLOADED, UPLOADING, WAITING
        }


        public LocalRecord newRecord;
        public LocalSong song;
        public Result result = Result.NOT_UPLOADED;
        public boolean isSelected = true;
        public boolean oldMaxCombo;
        public final int recordNumber;
        public float oldRate;
        public int taskNumber;

        public RecordData(int recordNumber) {
            this.recordNumber = recordNumber;
        }
    }


    private final List<RecordData> records = new CopyOnWriteArrayList<>();
    public final ScannerResultTableModel tableModel = new ScannerResultTableModel();
    public final TableRowSorter<TableModel> rowSorter = tableModel.newRowSorter();

    private RecordModel recordModel;
    private SongModel songModel;

    public void setModels(SongModel songModel, RecordModel recordModel) {
        this.recordModel = recordModel;
        this.songModel = songModel;
    }

    public void clearRecords() {
        records.clear();
        tableModel.fireTableDataChanged();
    }

    public void addRecords(List<ScannerTask> tasks) {
        List<RecordData> dataList = new ArrayList<>();

        tasks.forEach((task) -> {
            LocalSong song = task.song;

            task.getAnalyzedDataCellSet().forEach((cell) -> {
                AnalyzedData analyzedData = cell.getValue();

                // The rate will be negative when an OCRed rateText is invalid.
                if (analyzedData.rate == -1) {
                    return;
                }

                Api.Button button = cell.getRowKey().toApi();
                Api.Pattern pattern = cell.getColumnKey().toApi();
                float rate = analyzedData.rate;
                boolean maxCombo = analyzedData.isMaxCombo;

                LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, rate, maxCombo);
                LocalRecord oldRecord = recordModel.findSameRecord(newRecord);

                if (oldRecord != null && oldRecord.isUpdated(newRecord)) {
                    RecordData data = new RecordData(dataList.size());
                    data.newRecord = newRecord;
                    data.oldMaxCombo = oldRecord.maxCombo;
                    data.oldRate = oldRecord.rate;
                    data.song = song;
                    data.taskNumber = task.taskNumber;

                    dataList.add(data);
                }
            });
        });

        records.addAll(dataList);
        tableModel.fireTableDataChanged();
    }

    public void upload(Path accountPath, int delay) throws IOException, GeneralSecurityException {
        Account account = new Account(accountPath);
        RecordUploader api = Api.newRecordUploader(account.userNo, account.token);

        EnumSet<Result> filter = EnumSet.of(Result.CANCELED, Result.NOT_UPLOADED, Result.SUSPENDED);
        Queue<RecordData> queue =
                records.stream().filter((x) -> x.isSelected && filter.contains(x.result))
                        .collect(Collectors.toCollection(LinkedList::new));

        try {
            while (queue.peek() != null) {
                RecordData data = queue.poll();

                data.result = Result.UPLOADING;
                tableModel.fireTableRowsUpdated(data.recordNumber, data.recordNumber);

                RequestJson requestJson = recordToRequest(data.song, data.newRecord);
                api.upload(requestJson); // Throw an RuntimeException when an error occurs.

                data.result = api.getResult() ? Result.UPLOADED : Result.HIGHER_RECORD_EXISTS;
                recordModel.update(data.newRecord);
                tableModel.fireTableRowsUpdated(data.recordNumber, data.recordNumber);

                TimeUnit.MILLISECONDS.sleep(delay);
            }
        } catch (InterruptedException e) {
            while (queue.peek() != null) {
                RecordData data = queue.poll();
                data.result = Result.CANCELED;
            }
        } finally {
            while (queue.peek() != null) {
                RecordData data = queue.poll();
                data.result = Result.SUSPENDED;
            }
        }

        recordModel.save();

        tableModel.fireTableDataChanged();
    }

    private RequestJson recordToRequest(LocalSong song, LocalRecord record) {
        String title = song.remote_title();
        if (title == null) {
            title = song.title();
        }

        RequestJson requestJson =
                new RequestJson(title, record.button, record.pattern, record.rate, record.maxCombo);
        if (songModel.duplicateTitleSet().contains(song.id())) {
            requestJson.composer = song.composer();
        }

        return requestJson;
    }

    protected class ScannerResultTableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 3174993439149836273L;

        private static final String NO_COLUMN = "No";
        private static final String TASKNO_COLUMN = "TaskNo";
        private static final String TITLE_COLUMN = "Title";
        private static final String BUTTON_COLUMN = "Button";
        private static final String PATTERN_COLUMN = "Pattern";
        private static final String OMAX_COLUMN = "OMax";
        private static final String OLD_COLUMN = "Old";
        private static final String NEW_COLUMN = "New";
        private static final String NMAX_COLUMN = "NMax";
        private static final String UPLOAD_COLUMN = "Upload";

        private static final List<String> COLUMNS =
                List.of(NO_COLUMN, TASKNO_COLUMN, TITLE_COLUMN, "Composer", "Dlc", BUTTON_COLUMN,
                        PATTERN_COLUMN, OMAX_COLUMN, OLD_COLUMN, NEW_COLUMN, NMAX_COLUMN,
                        UPLOAD_COLUMN, "Upload result");

        private static final int NO_COLUMN_INDEX = COLUMNS.indexOf(NO_COLUMN);
        private static final int TASKNO_COLUMN_INDEX = COLUMNS.indexOf(TASKNO_COLUMN);
        private static final int TITLE_COLUMN_INDEX = COLUMNS.indexOf(TITLE_COLUMN);
        private static final int BUTTON_COLUMN_INDEX = COLUMNS.indexOf(BUTTON_COLUMN);
        private static final int PATTERN_COLUMN_INDEX = COLUMNS.indexOf(PATTERN_COLUMN);
        private static final int OMAX_COLUMN_INDEX = COLUMNS.indexOf(OMAX_COLUMN);
        private static final int OLD_COLUMN_INDEX = COLUMNS.indexOf(OLD_COLUMN);
        private static final int NEW_COLUMN_INDEX = COLUMNS.indexOf(NEW_COLUMN);
        private static final int NMAX_COLUMN_INDEX = COLUMNS.indexOf(NMAX_COLUMN);
        private static final int UPLOAD_COLUMN_INDEX = COLUMNS.indexOf(UPLOAD_COLUMN);

        private static final Set<Integer> BOOLEAN_COLUMNS =
                Set.of(OMAX_COLUMN_INDEX, NMAX_COLUMN_INDEX);
        private static final Set<Integer> FLOAT_COLUMNS =
                Set.of(OLD_COLUMN_INDEX, NEW_COLUMN_INDEX);
        private static final Set<Integer> INT_COLUMNS =
                Set.of(NO_COLUMN_INDEX, TASKNO_COLUMN_INDEX, BUTTON_COLUMN_INDEX);

        private static final String ERROR_STRING = "ERROR";

        public ScannerResultTableRowSorter newRowSorter() {
            return new ScannerResultTableRowSorter(this);
        }

        private String resultToString(Result result) {
            return switch (result) {
                case CANCELED -> "canceled";
                case HIGHER_RECORD_EXISTS -> "higher record exists";
                case NOT_UPLOADED -> "";
                case SUSPENDED -> "suspended";
                case UPLOADED -> "uploaded";
                case UPLOADING -> "uploading";
                case WAITING -> "waiting";
            };
        }

        @Override
        public int getRowCount() {
            return records.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.size();
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS.get(column);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (BOOLEAN_COLUMNS.contains(columnIndex)) {
                return Boolean.class;
            }
            if (FLOAT_COLUMNS.contains(columnIndex)) {
                return Float.class;
            }
            if (INT_COLUMNS.contains(columnIndex)) {
                return Integer.class;
            }
            if (columnIndex == BUTTON_COLUMN_INDEX) {
                return Button.class;
            }
            if (columnIndex == PATTERN_COLUMN_INDEX) {
                return Pattern.class;
            }
            if (columnIndex == UPLOAD_COLUMN_INDEX) {
                return Boolean.class;
            }

            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == UPLOAD_COLUMN_INDEX;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            RecordData data = records.get(rowIndex);
            if (data == null) {
                return ERROR_STRING;
            }

            return switch (columnIndex) {
                case 0 -> data.recordNumber;
                case 1 -> data.taskNumber;
                case 2 -> data.song.title();
                case 3 -> data.song.composer();
                case 4 -> data.song.dlc();
                case 5 -> Button.valueOf(data.newRecord.button);
                case 6 -> Pattern.valueOf(data.newRecord.pattern);
                case 7 -> data.oldMaxCombo;
                case 8 -> data.oldRate;
                case 9 -> data.newRecord.rate;
                case 10 -> data.newRecord.maxCombo;
                case 11 -> data.isSelected;
                case 12 -> resultToString(data.result);
                default -> ERROR_STRING;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == UPLOAD_COLUMN_INDEX && aValue instanceof Boolean isSelected) {
                RecordData data = records.get(rowIndex);
                data.isSelected = isSelected;

                fireTableRowsUpdated(rowIndex, rowIndex);
            }
        }

        public static class ScannerResultTableRowSorter extends TableRowSorter<TableModel> {
            public ScannerResultTableRowSorter(TableModel tableModel) {
                super(tableModel);

                setComparator(TITLE_COLUMN_INDEX, new TitleComparator());
                setComparator(BUTTON_COLUMN_INDEX, Comparator.comparingInt(Button::getWeight));
                setComparator(PATTERN_COLUMN_INDEX, Comparator.comparingInt(Pattern::getWeight));
                setComparator(OMAX_COLUMN_INDEX, Comparator.reverseOrder());
                setComparator(OLD_COLUMN_INDEX, Comparator.reverseOrder());
                setComparator(NEW_COLUMN_INDEX, Comparator.reverseOrder());
                setComparator(NMAX_COLUMN_INDEX, Comparator.reverseOrder());
                setComparator(UPLOAD_COLUMN_INDEX, Comparator.reverseOrder());

                setSortKeys(List.of(new RowSorter.SortKey(NO_COLUMN_INDEX, SortOrder.ASCENDING)));
            }
        }
    }
}
