package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.util.LocalSongComparator;
import com.github.johypark97.varchivemacro.lib.common.database.util.TitleComparator;
import com.github.johypark97.varchivemacro.macro.gui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.ScannerTask.AnalyzedData;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class ResultManager {
    public static class RecordData {
        public LocalRecord newRecord;
        public LocalRecord oldRecord;
        public LocalSong song;
        public boolean isSelected = true;
        public int taskNumber;
    }


    private final List<RecordData> records = new CopyOnWriteArrayList<>();
    public final ScannerResultTableModel tableModel = new ScannerResultTableModel();
    public final TableRowSorter<TableModel> rowSorter;

    public final RecordModel recordModel;

    private static final float PERFECT_SCORE = 100f;


    public ResultManager(RecordModel recordModel) {
        this.recordModel = recordModel;

        rowSorter = tableModel.newRowSorter();
    }

    public void clearRecords() {
        records.clear();
        tableModel.fireTableDataChanged();
    }

    public void addRecordFromTask(ScannerTask task) {
        LocalSong song = task.song;

        List<RecordData> dataList = new ArrayList<>();
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
            float score = analyzedData.rate;
            int maxCombo = analyzedData.isMaxCombo ? 1 : 0;

            LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, score, maxCombo);
            LocalRecord oldRecord = recordModel.findSameRecord(newRecord);

            if (oldRecord == null || oldRecord.isUpdated(newRecord)) {
                RecordData data = new RecordData();
                data.newRecord = newRecord;
                data.oldRecord = oldRecord;
                data.song = song;
                data.taskNumber = task.taskNumber;

                dataList.add(data);
            }
        });

        dataList.sort(new Comparator<>() {
            private final LocalSongComparator superComparator = new LocalSongComparator();

            @Override
            public int compare(RecordData o1, RecordData o2) {
                return superComparator.compare(o1.song, o2.song);
            }
        });

        records.addAll(dataList);
        tableModel.fireTableDataChanged();
    }

    private String toScoreText(LocalRecord record) {
        if (record == null || record.score == 0) {
            return "";
        }

        String mark = "";
        if (record.score == PERFECT_SCORE) {
            mark = " (P)";
        } else if (record.maxCombo != 0) {
            mark = " (M)";
        }

        return String.format("%.2f%s", record.score, mark);
    }


    protected class ScannerResultTableModel extends AbstractTableModel {
        @Serial
        private static final long serialVersionUID = 3174993439149836273L;

        private static final List<String> COLUMNS =
                List.of("No", "TaskNo", "Title", "Composer", "Dlc", "Button", "Pattern", "Old",
                        "New", "Upload");

        private static final String ERROR_STRING = "ERROR";
        private static final int UPLOAD_COLUMN_INDEX = COLUMNS.indexOf("Upload");

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
            return (columnIndex == UPLOAD_COLUMN_INDEX)
                    ? Boolean.class
                    : super.getColumnClass(columnIndex);
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
                case 0 -> rowIndex + 1;
                case 1 -> data.taskNumber;
                case 2 -> data.song.title();
                case 3 -> data.song.composer();
                case 4 -> data.song.dlc();
                case 5 -> data.newRecord.button.toString();
                case 6 -> data.newRecord.pattern.toString();
                case 7 -> toScoreText(data.oldRecord);
                case 8 -> toScoreText(data.newRecord);
                case 9 -> data.isSelected;
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

                Comparator<Integer> intComparator = Integer::compare;
                Comparator<String> titleComparator = new TitleComparator();

                setComparator(COLUMNS.indexOf("No"), intComparator);
                setComparator(COLUMNS.indexOf("TaskNo"), intComparator);
                setComparator(COLUMNS.indexOf("Title"), titleComparator);

                setSortKeys(
                        List.of(new RowSorter.SortKey(COLUMNS.indexOf("No"), SortOrder.ASCENDING)));
            }
        }
    }
}
