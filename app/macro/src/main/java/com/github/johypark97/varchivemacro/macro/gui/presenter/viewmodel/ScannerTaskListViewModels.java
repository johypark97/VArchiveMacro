package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.macro.core.scanner.ScannerTaskStatus;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Model;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ViewModel;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

public interface ScannerTaskListViewModels {
    enum ColumnKey {COMPOSER, COUNT, DLC, INDEX, SONG_NUMBER, STATUS, TAB, TASK_NUMBER, TITLE}


    class ColumnLookup implements TableColumnLookup<ColumnKey> {
        private final BiMap<ColumnKey, Integer> indexMap;
        private final Map<ColumnKey, String> nameMap;
        public final int count;

        public ColumnLookup() {
            count = ColumnKey.values().length;
            indexMap = createIndexMap();
            nameMap = createNameMap();
        }

        private BiMap<ColumnKey, Integer> createIndexMap() {
            List<ColumnKey> list = new ArrayList<>();
            list.add(ColumnKey.INDEX);
            list.add(ColumnKey.COUNT);
            list.add(ColumnKey.TASK_NUMBER);
            list.add(ColumnKey.TITLE);
            list.add(ColumnKey.COMPOSER);
            list.add(ColumnKey.DLC);
            list.add(ColumnKey.TAB);
            list.add(ColumnKey.SONG_NUMBER);
            list.add(ColumnKey.STATUS);

            ImmutableBiMap.Builder<ColumnKey, Integer> builder = ImmutableBiMap.builder();
            int indexCount = list.size();
            for (int i = 0; i < indexCount; ++i) {
                builder.put(list.get(i), i);
            }

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            ImmutableMap.Builder<ColumnKey, String> builder = ImmutableMap.builder();

            for (ColumnKey key : ColumnKey.values()) {
                String name = switch (key) {
                    case COMPOSER -> "Composer";
                    case COUNT -> "Count";
                    case DLC -> "Dlc";
                    case INDEX -> "Index";
                    case SONG_NUMBER -> "SongNo";
                    case STATUS -> "Status";
                    case TAB -> "Tab";
                    case TASK_NUMBER -> "TaskNo";
                    case TITLE -> "Title";
                };
                builder.put(key, name);
            }

            return builder.build();
        }

        @Override
        public ColumnKey getKey(int index) {
            return indexMap.inverse().get(index);
        }

        @Override
        public int getIndex(ColumnKey key) {
            return indexMap.get(key);
        }

        @Override
        public String getName(ColumnKey key) {
            return nameMap.get(key);
        }
    }


    class ScannerTaskListViewModel extends AbstractTableModel
            implements TableModelWithLookup<ColumnKey>, ViewModel {
        @Serial
        private static final long serialVersionUID = 2595265577036844112L;

        private static final ColumnLookup COLUMN_LOOKUP = new ColumnLookup();
        private static final String ERROR_STRING = "ERROR";

        private Model model;

        private String convertStatus(ScannerTaskStatus status) {
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
        public void onLinkModel(Model model) {
            this.model = model;
        }

        @Override
        public void onDataChanged() {
            fireTableDataChanged();
        }

        @Override
        public void onRowsInserted(int row) {
            fireTableRowsInserted(row, row);
        }

        @Override
        public void onRowsUpdated(int row) {
            fireTableRowsUpdated(row, row);
        }

        @Override
        public TableColumnLookup<ColumnKey> getTableColumnLookup() {
            return COLUMN_LOOKUP;
        }

        @Override
        public int getRowCount() {
            return model.getCount();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_LOOKUP.count;
        }

        @Override
        public String getColumnName(int column) {
            ColumnKey key = COLUMN_LOOKUP.getKey(column);
            return COLUMN_LOOKUP.getName(key);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ResponseData data = model.getData(rowIndex);
            if (data == null) {
                return ERROR_STRING;
            }

            return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
                case COMPOSER -> data.composer;
                case COUNT -> data.count;
                case DLC -> data.dlc;
                case INDEX -> data.index;
                case SONG_NUMBER -> data.index + 1 + " / " + data.count;
                case STATUS -> convertStatus(data.status);
                case TAB -> data.tab;
                case TASK_NUMBER -> data.taskNumber;
                case TITLE -> data.title;
            };
        }
    }
}
