package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableModelWithLookup;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskStatus;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Model;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ViewModel;
import com.google.common.collect.BiMap;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public interface ScannerTaskListViewModels {
    enum ColumnKey {
        ACCURACY, COMPOSER, DISTANCE, DLC, SCANNED_TITLE, SELECTED, STATUS, TAB, TASK_NUMBER, TITLE
    }


    class ColumnLookup implements TableColumnLookup<ColumnKey> {
        private final BiMap<ColumnKey, Integer> indexMap;
        private final Map<ColumnKey, String> nameMap;

        public ColumnLookup() {
            indexMap = createIndexMap();
            nameMap = createNameMap();
        }

        private BiMap<ColumnKey, Integer> createIndexMap() {
            IndexMapBuilder<ColumnKey> builder = new IndexMapBuilder<>();

            builder.add(ColumnKey.SELECTED);
            builder.add(ColumnKey.TASK_NUMBER);
            builder.add(ColumnKey.ACCURACY);
            builder.add(ColumnKey.DISTANCE);
            builder.add(ColumnKey.SCANNED_TITLE);
            builder.add(ColumnKey.TITLE);
            builder.add(ColumnKey.COMPOSER);
            builder.add(ColumnKey.DLC);
            builder.add(ColumnKey.TAB);
            builder.add(ColumnKey.STATUS);

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            NameMapBuilder<ColumnKey> builder = new NameMapBuilder<>();

            builder.setEnumClass(ColumnKey.class);
            builder.setConverter((x) -> switch (x) {
                case ACCURACY -> "Accuracy";
                case COMPOSER -> "Composer";
                case DISTANCE -> "Differences";
                case DLC -> "Dlc";
                case SCANNED_TITLE -> "ScannedTitle";
                case SELECTED -> "isSelected";
                case STATUS -> "Status";
                case TAB -> "Tab";
                case TASK_NUMBER -> "TaskNo";
                case TITLE -> "Title";
            });

            return builder.build();
        }

        @Override
        public int getCount() {
            return ColumnKey.values().length;
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
                case EXCEPTION -> "error occurred";
                case NOT_FOUND -> "not found";
                case FOUND -> "found";
                case NONE -> "";
                case DUPLICATED -> "duplicated";
                case WAITING -> "waiting";
            };
        }

        @Override
        public void onLinkModel(Model model) {
            this.model = model;
        }

        @Override
        public void onDataChanged() {
            SwingUtilities.invokeLater(this::fireTableDataChanged);
        }

        @Override
        public void onRowsInserted(int row) {
            SwingUtilities.invokeLater(() -> fireTableRowsInserted(row, row));
        }

        @Override
        public void onRowsUpdated(int row) {
            SwingUtilities.invokeLater(() -> fireTableRowsUpdated(row, row));
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
            return COLUMN_LOOKUP.getCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ResponseData data = model.getData(rowIndex);
            if (data == null) {
                return ERROR_STRING;
            }

            return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
                case ACCURACY -> data.accuracy * 100;
                case COMPOSER -> data.composer;
                case DISTANCE -> data.distance;
                case DLC -> data.dlc;
                case SCANNED_TITLE -> data.scannedTitle;
                case SELECTED -> data.selected;
                case STATUS -> convertStatus(data.status);
                case TAB -> data.tab;
                case TASK_NUMBER -> data.taskNumber;
                case TITLE -> data.title;
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
                case ACCURACY -> Float.class;
                case COMPOSER -> String.class;
                case DISTANCE -> Integer.class;
                case DLC -> String.class;
                case SCANNED_TITLE -> String.class;
                case SELECTED -> Boolean.class;
                case STATUS -> String.class;
                case TAB -> String.class;
                case TASK_NUMBER -> Integer.class;
                case TITLE -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == COLUMN_LOOKUP.getIndex(ColumnKey.SELECTED);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == COLUMN_LOOKUP.getIndex(ColumnKey.SELECTED)
                    && aValue instanceof Boolean value) {
                model.updateSelected(rowIndex, value);
            }
        }

        public static class TaskRowSorter extends TableRowSorter<ScannerTaskListViewModel> {
            public TaskRowSorter(ScannerTaskListViewModel viewModel) {
                super(viewModel);

                setComp(ColumnKey.ACCURACY, Comparator.reverseOrder());
                setComp(ColumnKey.SCANNED_TITLE, new TitleComparator());
                setComp(ColumnKey.SELECTED, Comparator.reverseOrder());
                setComp(ColumnKey.TITLE, new TitleComparator());

                int taskNumberIndex = COLUMN_LOOKUP.getIndex(ColumnKey.TASK_NUMBER);
                setSortKeys(List.of(new SortKey(taskNumberIndex, SortOrder.ASCENDING)));
            }

            private void setComp(ColumnKey key, Comparator<?> comparator) {
                setComparator(COLUMN_LOOKUP.getIndex(key), comparator);
            }
        }
    }
}
