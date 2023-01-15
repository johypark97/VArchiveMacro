package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public interface IDbManager {
    interface Presenter {
        void start();

        void loadSongs();

        void updateFilter();

        void checkSongs();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void showDialog(String title, int messageType, Object... messages);

        String getSongsFileText();

        void setSongsTableModel(TableModel tableModel);

        void setSongsTableRowSorter(TableRowSorter<TableModel> tableRowSorter);

        void setSongsTableFilterColumnItems(String... items);

        String getSongsTableFilterColumn();

        String getSongsTableFilterText();

        void setCheckerResultText(String value);
    }
}
