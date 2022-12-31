package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DatabaseModel;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

public class DbManagerPresenter implements IDbManager.Presenter {
    // model
    private DatabaseModel databaseModel;

    // view
    public final IDbManager.View view;

    public DbManagerPresenter(IDbManager.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        view.showView();
    }

    @Override
    public void loadDatabase() {
        try {
            Path path = Path.of(view.getText_databaseFileTextField());
            databaseModel = new DatabaseModel(path);
        } catch (JsonSyntaxException e) {
            view.showDialog("Json Syntax Error", JOptionPane.ERROR_MESSAGE, "Invalid Json syntax");
            return;
        } catch (JsonIOException e) {
            view.showDialog("Json IO Error", JOptionPane.ERROR_MESSAGE, "Json file error");
            return;
        } catch (IOException e) {
            view.showDialog("IO Error", JOptionPane.ERROR_MESSAGE, "Cannot read the file");
            return;
        } catch (InvalidPathException e) {
            view.showDialog("Path Error", JOptionPane.ERROR_MESSAGE, "Invalid path");
            return;
        }

        view.setTableModel_viewerTabTable(databaseModel.tableModel);
        view.setTableRowSorter_viewerTabTable(databaseModel.tableRowSorter);
        view.setItems_viewerTabFilterComboBox(databaseModel.getFilterableColumns());
    }

    @Override
    public void updateFilter() {
        String column = view.getText_viewerTabFilterComboBox();
        String pattern = view.getText_viewerTabFilterTextField();

        databaseModel.setFilter(pattern, column);
    }
}
