package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import java.util.List;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public interface IDbManager {
    interface Presenter {
        void start();

        void stop();

        void loadSongs();

        void updateFilter();

        void checkSongs();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void disposeView();

        void showErrorDialog(String message);

        String getSongsFileText();

        void setSongsTableModel(TableModel tableModel);

        void setSongsTableRowSorter(TableRowSorter<TableModel> tableRowSorter);

        void setSongsTableFilterColumnItems(List<String> items);

        String getSongsTableFilterColumn();

        String getSongsTableFilterText();

        void setCheckerResultText(String value);
    }
}
