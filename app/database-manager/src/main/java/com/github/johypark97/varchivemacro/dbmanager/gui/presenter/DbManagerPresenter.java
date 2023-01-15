package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DatabaseModel;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import javax.swing.JOptionPane;

public class DbManagerPresenter implements IDbManager.Presenter {
    // model
    private final DatabaseModel databaseModel = new DatabaseModel();

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
    public void loadSongs() {
        try {
            Path path = Path.of(view.getSongsFileText());
            databaseModel.loadSongs(path);
        } catch (IOException e) {
            view.showDialog("IO Error", JOptionPane.ERROR_MESSAGE, "Cannot read the file");
            return;
        } catch (RuntimeException e) {
            view.showDialog("Runtime Exception", JOptionPane.ERROR_MESSAGE, e.getMessage());
            return;
        }

        view.setSongsTableModel(databaseModel.tableModel);
        view.setSongsTableRowSorter(databaseModel.tableRowSorter);
        view.setSongsTableFilterColumnItems(databaseModel.getFilterableColumns());
    }

    @Override
    public void updateFilter() {
        String column = view.getSongsTableFilterColumn();
        String pattern = view.getSongsTableFilterText();

        databaseModel.setFilter(pattern, column);
    }

    @Override
    public void checkSongs() {
        if (!databaseModel.isSongLoaded()) {
            view.showDialog("Songs file is not loaded", JOptionPane.ERROR_MESSAGE,
                    "Songs file is not loaded");
            return;
        }

        try {
            databaseModel.checkRemote();
        } catch (GeneralSecurityException e) {
            view.showDialog("TLS Error", JOptionPane.ERROR_MESSAGE, "TLS Init Error");
            return;
        } catch (IOException e) {
            view.showDialog("Network Error", JOptionPane.ERROR_MESSAGE, e.getMessage());
            return;
        } catch (InterruptedException e) {
            return;
        } catch (RuntimeException e) {
            view.showDialog("Runtime Exception", JOptionPane.ERROR_MESSAGE, e.getMessage());
            return;
        }

        Gson gson = newGsonBuilder_dump().create();
        StringBuilder builder = new StringBuilder();

        if (!databaseModel.conflict.isEmpty()) {
            builder.append("======== conflict ========\n");
            builder.append(gson.toJson(databaseModel.conflict));
        }

        if (!databaseModel.unclassified.isEmpty()) {
            if (!builder.isEmpty()) {
                builder.append("\n\n");
            }

            builder.append("======== unclassified ========\n");
            builder.append(gson.toJson(databaseModel.unclassified));
        }

        if (builder.isEmpty()) {
            builder.append("There are no conflicts or unclassified.");
        }

        view.setCheckerResultText(builder.toString());
    }
}
