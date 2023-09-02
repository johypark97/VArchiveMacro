package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.OcrTesterModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.OcrTesterModel.OcrTesterData;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModelColumn.ColumnKey;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModelColumn.ColumnLookup;
import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import java.io.Serial;
import java.util.Comparator;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class DefaultOcrTesterViewModel extends AbstractTableModel implements OcrTesterViewModel {
    @Serial
    private static final long serialVersionUID = 8568884217344797909L;

    private static final String ERROR_STRING = "ERROR";

    private transient final ColumnLookup COLUMN_LOOKUP = new ColumnLookup();

    public transient OcrTesterModel ocrTesterModel;

    public DefaultOcrTesterViewModel(OcrTesterModel ocrTesterModel) {
        this.ocrTesterModel = ocrTesterModel;
    }

    @Override
    public int getRowCount() {
        return ocrTesterModel.getCount();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_LOOKUP.getCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OcrTesterData data = ocrTesterModel.getData(rowIndex);
        if (data == null) {
            return ERROR_STRING;
        }

        return switch (COLUMN_LOOKUP.getKey(columnIndex)) {
            case ACCURACY -> data.getAccuracy() * 100;
            case DISTANCE -> data.getDistance();
            case NORM_SCANNED_TITLE -> data.getScannedNormalizedTitle();
            case NOTE -> data.getNote();
            case PASS -> data.getTestSong().equals(data.getRecognizedSong());
            case RECOGNIZED_SONG_COMPOSER ->
                    (data.getRecognizedSong()) != null ? data.getRecognizedSong().composer() : "";
            case RECOGNIZED_SONG_ID ->
                    (data.getRecognizedSong()) != null ? data.getRecognizedSong().id() : -1;
            case RECOGNIZED_SONG_TITLE ->
                    (data.getRecognizedSong()) != null ? data.getRecognizedSong().title() : "";
            case TEST_SONG_COMPOSER -> data.getTestSong().composer();
            case TEST_SONG_DLC -> data.getTestSong().dlc();
            case TEST_SONG_DLC_TAB -> data.getTestSong().dlcTab();
            case TEST_SONG_ID -> data.getTestSong().id();
            case TEST_SONG_NORM_TITLE -> data.getTestSong_normalizedTitle();
            case TEST_SONG_TITLE -> data.getTestSong().title();
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
            case DISTANCE -> Integer.class;
            case NORM_SCANNED_TITLE -> String.class;
            case NOTE -> String.class;
            case PASS -> Boolean.class;
            case RECOGNIZED_SONG_COMPOSER -> String.class;
            case RECOGNIZED_SONG_ID -> Integer.class;
            case RECOGNIZED_SONG_TITLE -> String.class;
            case TEST_SONG_COMPOSER -> String.class;
            case TEST_SONG_DLC -> String.class;
            case TEST_SONG_DLC_TAB -> String.class;
            case TEST_SONG_ID -> Integer.class;
            case TEST_SONG_NORM_TITLE -> String.class;
            case TEST_SONG_TITLE -> String.class;
        };
    }

    @Override
    public TableColumnLookup<ColumnKey> getTableColumnLookup() {
        return COLUMN_LOOKUP;
    }

    @Override
    public RowSorter<OcrTesterViewModel> getRowSorter() {
        return new DefaultOcrTesterViewModelRowSorter(this);
    }

    @Override
    public void notifyDataUpdated() {
        SwingUtilities.invokeLater(this::fireTableDataChanged);
    }

    @Override
    public void notifyDataAdded(int row) {
        SwingUtilities.invokeLater(() -> fireTableRowsInserted(row, row));
    }

    private class DefaultOcrTesterViewModelRowSorter extends TableRowSorter<OcrTesterViewModel> {
        public DefaultOcrTesterViewModelRowSorter(OcrTesterViewModel tableModel) {
            super(tableModel);

            Comparator<Integer> intComparator = Integer::compare;
            Comparator<String> strComparator = String::compareTo;
            Comparator<String> titleComparator = new TitleComparator();

            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.ACCURACY), Comparator.reverseOrder());
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.DISTANCE), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.NORM_SCANNED_TITLE), titleComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.NOTE), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.PASS), Comparator.reverseOrder());
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.RECOGNIZED_SONG_COMPOSER),
                    strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.RECOGNIZED_SONG_ID), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.RECOGNIZED_SONG_TITLE), titleComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_COMPOSER), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_DLC), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_DLC_TAB), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_ID), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_NORM_TITLE), titleComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_TITLE), titleComparator);

            int idKey = COLUMN_LOOKUP.getIndex(ColumnKey.TEST_SONG_ID);
            setSortKeys(List.of(new RowSorter.SortKey(idKey, SortOrder.ASCENDING)));
        }
    }
}
