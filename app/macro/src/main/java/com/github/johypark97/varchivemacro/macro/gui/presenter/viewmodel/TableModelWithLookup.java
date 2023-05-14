package com.github.johypark97.varchivemacro.macro.gui.presenter.viewmodel;

import javax.swing.table.TableModel;

public interface TableModelWithLookup<T> extends TableModel {
    TableColumnLookup<T> getTableColumnLookup();
}
