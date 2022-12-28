package com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct;

import com.github.johypark97.varchivemacro.dbmanager.database.util.TitleComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class DatabaseTableRowSorter extends TableRowSorter<TableModel> {
    public DatabaseTableRowSorter(TableModel tableModel) {
        super(tableModel);

        Comparator<Integer> intComparator = Integer::compare;
        Comparator<String> strComparator = String::compareTo;
        Comparator<String> titleComparator = new TitleComparator();

        setComparator(DatabaseTableModel.COLUMNS.indexOf("id"), intComparator);
        setComparator(DatabaseTableModel.COLUMNS.indexOf("title"), titleComparator);
        setComparator(DatabaseTableModel.COLUMNS.indexOf("remote_title"), titleComparator);
        setComparator(DatabaseTableModel.COLUMNS.indexOf("composer"), strComparator);
        setComparator(DatabaseTableModel.COLUMNS.indexOf("dlc"), strComparator);
        setSortKeys(getSortKeys("title", "dlc", "composer"));
    }

    public void setFilter(String pattern, String column) {
        int index = DatabaseTableModel.COLUMNS.indexOf(column);
        setRowFilter(new CaseInsensitiveRegexFilter(pattern, index));
    }

    private List<SortKey> getSortKeys(String... columns) {
        List<SortKey> sortKeys = new ArrayList<>();

        for (String i : columns) {
            int index = DatabaseTableModel.COLUMNS.indexOf(i);
            sortKeys.add(new RowSorter.SortKey(index, SortOrder.ASCENDING));
        }

        return sortKeys;
    }
}


class CaseInsensitiveRegexFilter extends RowFilter<TableModel, Integer> {
    private static final String EMPTY_STRING = "";

    private Matcher matcher;
    private final int column;

    public CaseInsensitiveRegexFilter(String pattern, int column) {
        if (pattern != null && !pattern.isBlank()) {
            try {
                matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(EMPTY_STRING);
            } catch (PatternSyntaxException ignored) {
            }
        }

        this.column = column;
    }

    @Override
    public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
        if (matcher == null || column < 0 || column >= entry.getValueCount()) {
            return true;
        }

        return matcher.reset(entry.getStringValue(column)).find();
    }
}
