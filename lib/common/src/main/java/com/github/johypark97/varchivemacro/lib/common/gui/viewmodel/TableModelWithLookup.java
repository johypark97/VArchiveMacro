package com.github.johypark97.varchivemacro.lib.common.gui.viewmodel;

import javax.swing.table.TableModel;

public interface TableModelWithLookup<T> extends TableModel {
    TableColumnLookup<T> getTableColumnLookup();
}
