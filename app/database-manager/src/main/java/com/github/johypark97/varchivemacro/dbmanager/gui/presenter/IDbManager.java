package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import java.util.List;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public interface IDbManager {
    interface Presenter {
        void start();

        void stop();

        void loadDatabase(String path);

        void updateFilter();

        void validateDatabase();

        void checkRemote();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void disposeView();

        void showErrorDialog(String message);

        void setSongsTableModel(TableModel tableModel);

        void setSongsTableRowSorter(TableRowSorter<TableModel> tableRowSorter);

        void setSongsTableFilterColumnItems(List<String> items);

        String getSongsTableFilterColumn();

        String getSongsTableFilterText();

        void setValidatorResultText(String value);

        void setCheckerResultText(String value);
    }
}
