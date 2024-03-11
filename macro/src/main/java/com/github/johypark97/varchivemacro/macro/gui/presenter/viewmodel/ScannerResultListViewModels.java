package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.lib.desktop.gui.viewmodel.TableColumnLookup;
import com.github.johypark97.varchivemacro.lib.desktop.gui.viewmodel.TableModelWithLookup;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.ResultManager.ResultStatus;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Model;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ViewModel;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.resource.MacroViewKey;
import com.google.common.collect.BiMap;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public interface ScannerResultListViewModels {
    enum ColumnKey {
        BUTTON, COMPOSER, DELTA_RATE, DLC, NEW_MAX_COMBO, NEW_RATE, OLD_MAX_COMBO, OLD_RATE, PATTERN, RESULT_NUMBER, STATUS, TASK_NUMBER, TITLE, UPLOAD
    }


    class ColumnLookup implements TableColumnLookup<ColumnKey> {
        private final Language lang = Language.getInstance();

        private final BiMap<ColumnKey, Integer> indexMap;
        private final Map<ColumnKey, String> nameMap;

        public ColumnLookup() {
            indexMap = createIndexMap();
            nameMap = createNameMap();
        }

        private BiMap<ColumnKey, Integer> createIndexMap() {
            IndexMapBuilder<ColumnKey> builder = new IndexMapBuilder<>();

            builder.add(ColumnKey.RESULT_NUMBER);
            builder.add(ColumnKey.TASK_NUMBER);
            builder.add(ColumnKey.TITLE);
            builder.add(ColumnKey.COMPOSER);
            builder.add(ColumnKey.DLC);
            builder.add(ColumnKey.BUTTON);
            builder.add(ColumnKey.PATTERN);
            builder.add(ColumnKey.OLD_MAX_COMBO);
            builder.add(ColumnKey.OLD_RATE);
            builder.add(ColumnKey.NEW_RATE);
            builder.add(ColumnKey.NEW_MAX_COMBO);
            builder.add(ColumnKey.DELTA_RATE);
            builder.add(ColumnKey.UPLOAD);
            builder.add(ColumnKey.STATUS);

            return builder.build();
        }

        private Map<ColumnKey, String> createNameMap() {
            NameMapBuilder<ColumnKey> builder = new NameMapBuilder<>();

            builder.setEnumClass(ColumnKey.class);
            builder.setConverter((x) -> switch (x) {
                case RESULT_NUMBER -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_RESULT_NUMBER);
                case TASK_NUMBER -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_TASK_NUMBER);
                case TITLE -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_TITLE);
                case COMPOSER -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_COMPOSER);
                case DLC -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_DLC);
                case BUTTON -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_BUTTON);
                case PATTERN -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_PATTERN);
                case OLD_MAX_COMBO -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_OLD_MAX_COMBO);
                case OLD_RATE -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_OLD_RATE);
                case NEW_RATE -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_NEW_RATE);
                case NEW_MAX_COMBO -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_NEW_MAX_COMBO);
                case DELTA_RATE -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_DELTA_RATE);
                case UPLOAD -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_UPLOAD);
                case STATUS -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS);
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


    class ScannerResultListViewModel extends AbstractTableModel
            implements TableModelWithLookup<ColumnKey>, ViewModel {
        @Serial
        private static final long serialVersionUID = 3174993439149836273L;

        private static final ColumnLookup COLUMN_LOOKUP = new ColumnLookup();
        private static final String ERROR_STRING = "ERROR";

        private final Language lang = Language.getInstance();

        private Model model;

        public ResultRowSorter createRowSorter() {
            return new ResultRowSorter(this);
        }

        private String statusToString(ResultStatus status) {
            return switch (status) {
                case CANCELED -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS_CANCELED);
                case HIGHER_RECORD_EXISTS ->
                        lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS_HIGHER_RECORD_EXISTS);
                case NOT_UPLOADED -> "";
                case SUSPENDED -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS_SUSPENDED);
                case UPLOADED -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS_UPLOADED);
                case UPLOADING -> lang.get(MacroViewKey.TAB_SCANNER_RESULT_TABLE_STATUS_UPLOADING);
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
