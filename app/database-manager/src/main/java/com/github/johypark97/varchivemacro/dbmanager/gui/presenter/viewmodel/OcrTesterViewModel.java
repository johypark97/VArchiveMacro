package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModelColumn.ColumnKey;
import com.github.johypark97.varchivemacro.lib.common.gui.viewmodel.TableModelWithLookup;
import javax.swing.RowSorter;

public interface OcrTesterViewModel extends TableModelWithLookup<ColumnKey> {
    RowSorter<OcrTesterViewModel> getRowSorter();

    void notifyDataUpdated();

    void notifyDataAdded(int row);
}
