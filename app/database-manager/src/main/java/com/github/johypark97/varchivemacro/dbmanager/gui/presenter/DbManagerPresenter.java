package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class DbManagerPresenter implements Presenter {
    // model
    private final DatabaseModel databaseModel = new DatabaseModel();

    // view
    private final Class<? extends View> viewClass;
    public View view;

    public DbManagerPresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    private void newView() {
        try {
            view = viewClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    @Override
    public synchronized void start() {
        if (view != null) {
            return;
        }
        newView();

        view.showView();
    }

    @Override
    public void stop() {
        view.disposeView();
    }

    @Override
    public void loadSongs() {
        try {
            Path path = Path.of(view.getSongsFileText());
            databaseModel.loadSongs(path);
        } catch (IOException e) {
            view.showErrorDialog("Cannot read the file");
            return;
        } catch (RuntimeException e) {
            view.showErrorDialog(e.getMessage());
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
            view.showErrorDialog("A songs json file not loaded");
            return;
        }

        try {
            databaseModel.checkRemote();
        } catch (GeneralSecurityException e) {
            view.showErrorDialog("TLS Init Error");
            return;
        } catch (IOException | RuntimeException e) {
            view.showErrorDialog(e.getMessage());
            return;
        } catch (InterruptedException e) {
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
