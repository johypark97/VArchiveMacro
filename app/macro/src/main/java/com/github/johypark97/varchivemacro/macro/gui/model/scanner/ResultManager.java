package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.util.TitleComparator;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.SongModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ResultManager.RecordData.Result;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.AnalyzedData;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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


    private static final int UPLOAD_DELAY = 20;

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

                Api.Button button = switch (cell.getRowKey()) {
                    case _4 -> Api.Button._4;
                    case _5 -> Api.Button._5;
                    case _6 -> Api.Button._6;
                    case _8 -> Api.Button._8;
                };
                Api.Pattern pattern = switch (cell.getColumnKey()) {
                    case NM -> Api.Pattern.NM;
                    case HD -> Api.Pattern.HD;
                    case MX -> Api.Pattern.MX;
                    case SC -> Api.Pattern.SC;
                };
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

    public void upload(Path accountPath) throws IOException, GeneralSecurityException {
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

                TimeUnit.MILLISECONDS.sleep(UPLOAD_DELAY);
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

        private static final List<String> COLUMNS =
                List.of("No", "TaskNo", "Title", "Composer", "Dlc", "Button", "Pattern", "OMax",
                        "Old", "New", "NMax", "Upload", "Upload result");

        private static final Set<Integer> BOOLEAN_COLUMNS =
                Set.of(COLUMNS.indexOf("OMax"), COLUMNS.indexOf("NMax"));
        private static final Set<Integer> FLOAT_COLUMNS =
                Set.of(COLUMNS.indexOf("Old"), COLUMNS.indexOf("New"));
        private static final Set<Integer> INT_COLUMNS =
                Set.of(COLUMNS.indexOf("No"), COLUMNS.indexOf("TaskNo"), COLUMNS.indexOf("Button"));
        private static final int UPLOAD_COLUMN_INDEX = COLUMNS.indexOf("Upload");

        private static final String ERROR_STRING = "ERROR";

        public ScannerResultTableRowSorter newRowSorter() {
            return new ScannerResultTableRowSorter(this);
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
                case 5 -> data.newRecord.button.getValue();
                case 6 -> data.newRecord.pattern.getShortName();
                case 7 -> data.oldMaxCombo;
                case 8 -> data.oldRate;
                case 9 -> data.newRecord.rate;
                case 10 -> data.newRecord.maxCombo;
                case 11 -> data.isSelected;
                case 12 -> switch (data.result) {
                    case CANCELED -> "canceled";
                    case HIGHER_RECORD_EXISTS -> "higher record exists";
                    case NOT_UPLOADED -> "";
                    case SUSPENDED -> "suspended";
                    case UPLOADED -> "uploaded";
                    case UPLOADING -> "uploading";
                    case WAITING -> "waiting";
                };
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

                Comparator<String> patternComparator = new Comparator<>() {
                    private static final Map<String, Integer> PRIORITY =
                            Arrays.stream(Pattern.values()).collect(
                                    Collectors.toMap(Pattern::getShortName, Pattern::getWeight));

                    @Override
                    public int compare(String o1, String o2) {
                        return PRIORITY.getOrDefault(o1, -1) - PRIORITY.getOrDefault(o2, -1);
                    }
                };

                setComparator(COLUMNS.indexOf("Title"), new TitleComparator());
                setComparator(COLUMNS.indexOf("Pattern"), patternComparator);
                setComparator(COLUMNS.indexOf("OMax"), Comparator.reverseOrder());
                setComparator(COLUMNS.indexOf("Old"), Comparator.reverseOrder());
                setComparator(COLUMNS.indexOf("New"), Comparator.reverseOrder());
                setComparator(COLUMNS.indexOf("NMax"), Comparator.reverseOrder());
                setComparator(COLUMNS.indexOf("Upload"), Comparator.reverseOrder());

                setSortKeys(
                        List.of(new RowSorter.SortKey(COLUMNS.indexOf("No"), SortOrder.ASCENDING)));
            }
        }
    }
}
