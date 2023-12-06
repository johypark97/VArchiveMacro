package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.SongModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModelColumn.ColumnKey;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModelColumn.ColumnLookup;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableColumnLookup;
import java.io.Serial;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class DefaultSongViewModel extends AbstractTableModel implements SongViewModel {
    @Serial
    private static final long serialVersionUID = -7537639119862573233L;

    private static final String ERROR_STRING = "ERROR";

    private transient final ColumnLookup COLUMN_LOOKUP = new ColumnLookup();
    private transient final SongModel model;

    public transient TableRowSorter<SongViewModel> rowSorter;

    public DefaultSongViewModel(SongModel model) {
        this.model = model;
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
        LocalDlcSong song = model.getSong(rowIndex);
        if (song == null) {
            return ERROR_STRING;
        }

        return switch (columnIndex) {
            case 0 -> rowIndex + 1;
            case 1 -> song.id;
            case 2 -> song.title;
            case 3 -> song.remoteTitle;
            case 4 -> song.composer;
            case 5 -> song.dlc;
            case 6 -> song.dlcCode;
            case 7 -> song.dlcTab;
            case 8 -> song.priority;
            default -> ERROR_STRING;
        };
    }

    @Override
    public String getColumnName(int column) {
        ColumnKey key = COLUMN_LOOKUP.getKey(column);
        return COLUMN_LOOKUP.getName(key);
    }

    @Override
    public TableColumnLookup<ColumnKey> getTableColumnLookup() {
        return COLUMN_LOOKUP;
    }

    @Override
    public synchronized TableRowSorter<SongViewModel> getRowSorter() {
        if (rowSorter == null) {
            rowSorter = new DefaultSongViewModelRowSorter(this);
        }

        return rowSorter;
    }

    @Override
    public List<String> getFilterableColumnList() {
        return Arrays.stream(ColumnKey.values()).filter((x) -> !ColumnKey.ID.equals(x))
                .sorted((o1, o2) -> {
                    int a = COLUMN_LOOKUP.getIndex(o1);
                    int b = COLUMN_LOOKUP.getIndex(o2);
                    return Integer.compare(a, b);
                }).map(COLUMN_LOOKUP::getName).toList();
    }

    @Override
    public synchronized void setFilter(String columnName, String pattern) {
        if (rowSorter == null) {
            return;
        }

        for (ColumnKey key : ColumnKey.values()) {
            if (COLUMN_LOOKUP.getName(key).equals(columnName)) {
                int index = COLUMN_LOOKUP.getIndex(key);
                rowSorter.setRowFilter(new CaseInsensitiveRegexRowFilter(pattern, index));
            }
        }
    }

    private class DefaultSongViewModelRowSorter extends TableRowSorter<SongViewModel> {
        public DefaultSongViewModelRowSorter(SongViewModel tableModel) {
            super(tableModel);

            Comparator<Integer> intComparator = Integer::compare;
            Comparator<String> strComparator = String::compareTo;
            Comparator<String> titleComparator = new TitleComparator();

            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.COMPOSER), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.DLC), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.DLC_CODE), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.DLC_TAB), strComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.ID), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.NUMBER), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.PRIORITY), intComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.REMOTE_TITLE), titleComparator);
            setComparator(COLUMN_LOOKUP.getIndex(ColumnKey.TITLE), titleComparator);

            int numberKey = COLUMN_LOOKUP.getIndex(ColumnKey.NUMBER);
            setSortKeys(List.of(new RowSorter.SortKey(numberKey, SortOrder.ASCENDING)));
        }
    }
}
