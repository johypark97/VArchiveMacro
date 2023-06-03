package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Model;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ViewModel;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public interface ScannerTaskListViewModels {
    enum ColumnKey {
        COMPOSER, COUNT, DLC, INDEX, SCANNED_TITLE, SONG_NUMBER, STATUS, TAB, TASK_NUMBER, TITLE, VALID
    }


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
            list.add(ColumnKey.VALID);
            list.add(ColumnKey.TASK_NUMBER);
            list.add(ColumnKey.SCANNED_TITLE);
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
                    case SCANNED_TITLE -> "ScannedTitle";
                    case SONG_NUMBER -> "SongNo";
                    case STATUS -> "Status";
                    case TAB -> "Tab";
                    case TASK_NUMBER -> "TaskNo";
                    case TITLE -> "Title";
                    case VALID -> "isValid";
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

        public TaskRowSorter createRowSorter() {
            return new TaskRowSorter(this);
        }

        private String convertStatus(TaskStatus status) {
            return switch (status) {
                case ANALYZED -> "analyzed";
                case ANALYZING -> "analyzing";
                case CACHED -> "cached";
                case CAPTURED -> "captured";
                case EXCEPTION -> "error occurred";
                case NONE -> "";
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
                case SCANNED_TITLE -> data.scannedTitle;
                case SONG_NUMBER -> data.index + 1 + " / " + Math.abs(data.count);
                case STATUS -> convertStatus(data.status);
                case TAB -> data.tab;
                case TASK_NUMBER -> data.taskNumber;
                case TITLE -> data.title;
                case VALID -> data.valid;
            };
        }

        @Override
        public String getColumnName(int column) {
            ColumnKey key = COLUMN_LOOKUP.getKey(column);
            return COLUMN_LOOKUP.getName(key);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
                case COMPOSER, DLC, SCANNED_TITLE, SONG_NUMBER, STATUS, TAB, TITLE -> String.class;
                case COUNT, INDEX, TASK_NUMBER -> Integer.class;
                case VALID -> Boolean.class;
            };
        }

        public static class TaskRowSorter extends TableRowSorter<ScannerTaskListViewModel> {
            public TaskRowSorter(ScannerTaskListViewModel viewModel) {
                super(viewModel);

                setComp(ColumnKey.SCANNED_TITLE, new TitleComparator());
                setComp(ColumnKey.TITLE, new TitleComparator());
                setComp(ColumnKey.VALID, Comparator.reverseOrder());

                int taskNumberIndex = COLUMN_LOOKUP.getIndex(ColumnKey.TASK_NUMBER);
                setSortKeys(List.of(new SortKey(taskNumberIndex, SortOrder.ASCENDING)));
            }

            private void setComp(ColumnKey key, Comparator<?> comparator) {
                setComparator(COLUMN_LOOKUP.getIndex(key), comparator);
            }
        }
    }
}
