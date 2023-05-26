package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.ResultManager.RecordData.Result;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Model;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ViewModel;
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

public interface ScannerResultListViewModels {
    enum ColumnKey {
        BUTTON, COMPOSER, DELTA_RATE, DLC, NEW_MAX_COMBO, NEW_RATE, OLD_MAX_COMBO, OLD_RATE, PATTERN, RESULT_NUMBER, STATUS, TASK_NUMBER, TITLE, UPLOAD
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
            list.add(ColumnKey.RESULT_NUMBER);
            list.add(ColumnKey.TASK_NUMBER);
            list.add(ColumnKey.TITLE);
            list.add(ColumnKey.COMPOSER);
            list.add(ColumnKey.DLC);
            list.add(ColumnKey.BUTTON);
            list.add(ColumnKey.PATTERN);
            list.add(ColumnKey.OLD_MAX_COMBO);
            list.add(ColumnKey.OLD_RATE);
            list.add(ColumnKey.NEW_RATE);
            list.add(ColumnKey.NEW_MAX_COMBO);
            list.add(ColumnKey.DELTA_RATE);
            list.add(ColumnKey.UPLOAD);
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
                    case RESULT_NUMBER -> "ResultNo";
                    case TASK_NUMBER -> "TaskNo";
                    case TITLE -> "Title";
                    case COMPOSER -> "Composer";
                    case DLC -> "Dlc";
                    case BUTTON -> "Button";
                    case PATTERN -> "Pattern";
                    case OLD_MAX_COMBO -> "OMax";
                    case OLD_RATE -> "Old";
                    case NEW_RATE -> "New";
                    case NEW_MAX_COMBO -> "NMax";
                    case DELTA_RATE -> "Delta";
                    case UPLOAD -> "Upload";
                    case STATUS -> "Status";
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


    class ScannerResultListViewModel extends AbstractTableModel
            implements TableModelWithLookup<ColumnKey>, ViewModel {
        @Serial
        private static final long serialVersionUID = 3174993439149836273L;

        private static final ColumnLookup COLUMN_LOOKUP = new ColumnLookup();
        private static final String ERROR_STRING = "ERROR";

        private Model model;

        public ResultRowSorter createRowSorter() {
            return new ResultRowSorter(this);
        }

        private String statusToString(Result result) {
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
        public void onLinkModel(Model model) {
            this.model = model;
        }

        @Override
        public void onDataChanged() {
            fireTableDataChanged();
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
        public Class<?> getColumnClass(int columnIndex) {
            return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
                case BUTTON -> Button.class;
                case COMPOSER, DLC, STATUS, TITLE -> String.class;
                case DELTA_RATE, NEW_RATE, OLD_RATE -> Float.class;
                case NEW_MAX_COMBO, OLD_MAX_COMBO, UPLOAD -> Boolean.class;
                case PATTERN -> Pattern.class;
                case RESULT_NUMBER, TASK_NUMBER -> Integer.class;
            };
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == COLUMN_LOOKUP.getIndex(ColumnKey.UPLOAD);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ResponseData data = model.getData(rowIndex);
            if (data == null) {
                return ERROR_STRING;
            }

            return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
                case BUTTON -> data.button;
                case COMPOSER -> data.composer;
                case DELTA_RATE -> data.newRate - data.oldRate;
                case DLC -> data.dlc;
                case NEW_MAX_COMBO -> data.newMaxCombo;
                case NEW_RATE -> data.newRate;
                case OLD_MAX_COMBO -> data.oldMaxCombo;
                case OLD_RATE -> data.oldRate;
                case PATTERN -> data.pattern;
                case RESULT_NUMBER -> data.resultNumber;
                case STATUS -> statusToString(data.status);
                case TASK_NUMBER -> data.taskNumber;
                case TITLE -> data.title;
                case UPLOAD -> data.isSelected;
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == COLUMN_LOOKUP.getIndex(ColumnKey.UPLOAD)
                    && aValue instanceof Boolean value) {
                model.updateSelected(rowIndex, value);
            }
        }

        public static class ResultRowSorter extends TableRowSorter<ScannerResultListViewModel> {
            public ResultRowSorter(ScannerResultListViewModel viewModel) {
                super(viewModel);

                setComp(ColumnKey.BUTTON, Comparator.comparingInt(Button::getWeight));
                setComp(ColumnKey.DELTA_RATE, Comparator.reverseOrder());
                setComp(ColumnKey.NEW_MAX_COMBO, Comparator.reverseOrder());
                setComp(ColumnKey.NEW_RATE, Comparator.reverseOrder());
                setComp(ColumnKey.OLD_MAX_COMBO, Comparator.reverseOrder());
                setComp(ColumnKey.OLD_RATE, Comparator.reverseOrder());
                setComp(ColumnKey.PATTERN, Comparator.comparingInt(Pattern::getWeight));
                setComp(ColumnKey.TITLE, new TitleComparator());
                setComp(ColumnKey.UPLOAD, Comparator.reverseOrder());

                int resultNumberIndex = COLUMN_LOOKUP.getIndex(ColumnKey.RESULT_NUMBER);
                setSortKeys(List.of(new SortKey(resultNumberIndex, SortOrder.ASCENDING)));
            }

            private void setComp(ColumnKey key, Comparator<?> comparator) {
                setComparator(COLUMN_LOOKUP.getIndex(key), comparator);
            }
        }
    }
}
