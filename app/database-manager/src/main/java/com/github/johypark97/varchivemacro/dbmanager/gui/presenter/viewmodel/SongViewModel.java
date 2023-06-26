package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModelColumn.ColumnKey;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableModelWithLookup;
import java.util.List;
import javax.swing.table.TableRowSorter;

public interface SongViewModel extends TableModelWithLookup<ColumnKey> {
    TableRowSorter<SongViewModel> getRowSorter();

    List<String> getFilterableColumnList();

    void setFilter(String columnName, String pattern);
}
