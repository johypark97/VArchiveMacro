package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

public interface TableColumnLookup<T> {
    T getKey(int index);

    int getIndex(T key);

    String getName(T key);

    default int getIndexInView(JTable table, T key) {
        return table.convertColumnIndexToView(getIndex(key));
    }

    default TableColumn getColumn(JTable table, T key) {
        return table.getColumnModel().getColumn(getIndexInView(table, key));
    }
}
