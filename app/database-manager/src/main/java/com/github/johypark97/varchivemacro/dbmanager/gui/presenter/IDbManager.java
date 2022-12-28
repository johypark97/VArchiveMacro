package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public interface IDbManager {
    interface Presenter {
        void start();

        void loadDatabase();

        void updateFilter();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void showDialog(String title, int messageType, Object... messages);

        String getText_databaseFileTextField();

        void setTableModel_viewerTabTable(TableModel tableModel);

        void setTableRowSorter_viewerTabTable(TableRowSorter<TableModel> tableRowSorter);

        void setItems_viewerTabFilterComboBox(String... items);

        String getText_viewerTabFilterComboBox();

        String getText_viewerTabFilterTextField();
    }
}
