package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.Presenter;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.IDbManager.View;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DbManagerPresenter implements Presenter {
    // model
    private final DatabaseModel databaseModel = new DatabaseModel();

    // view
    private View view;
    private final Class<? extends View> viewClass;

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
    public void loadDatabase(String path) {
        try {
            databaseModel.load(Path.of(path));
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
    public void validateDatabase() {
        if (!databaseModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
            return;
        }

        Gson gson = newGsonBuilder_dump().create();
        StringBuilder builder = new StringBuilder();

        List<LocalSong> songs = databaseModel.getSongs();
        Set<String> dlcCodeSet = databaseModel.getDlcCodeSet();
        Set<String> dlcTabSet = databaseModel.getDlcTabSet();

        builder.append("-------- validation --------\n");
        builder.append('\n');

        builder.append("dlcCode (songs.json - dlcs.json)\n");
        {
            Set<String> set = songs.stream().map(LocalSong::dlcCode).collect(Collectors.toSet());
            if (set.equals(dlcCodeSet)) {
                builder.append("- ok\n");
            } else {
                builder.append("- failed\n");
                builder.append("songs.json: ").append(Sets.difference(set, dlcCodeSet))
                        .append('\n');
                builder.append("dlcs.json: ").append(Sets.difference(dlcCodeSet, set)).append('\n');
            }
        }
        builder.append('\n');

        builder.append("dlcCode list\n");
        {
            List<String> list = databaseModel.getDlcCodeList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("dlcTab (songs.json - tabs.json)\n");
        {
            Set<String> set = songs.stream().map(LocalSong::dlcTab).collect(Collectors.toSet());
            if (set.equals(dlcTabSet)) {
                builder.append("- ok\n");
            } else {
                builder.append("- failed\n");
                builder.append("songs.json: ").append(Sets.difference(set, dlcTabSet)).append('\n');
                builder.append("tabs.json: ").append(Sets.difference(dlcTabSet, set)).append('\n');
            }
        }
        builder.append('\n');

        builder.append("dlcTab list\n");
        {
            List<String> list = databaseModel.getDlcTabList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("-------- songs.json data check --------\n");
        builder.append('\n');

        builder.append("dlc (pack names) - dlcCode\n");
        {
            Map<String, Set<String>> map = new HashMap<>();
            songs.forEach((song) -> map.computeIfAbsent(song.dlc(), (x) -> new HashSet<>())
                    .add(song.dlcCode()));
            List<String> list =
                    map.entrySet().stream().map((x) -> x.getKey() + " - " + x.getValue()).sorted()
                            .toList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        builder.append("dlcTab - dlcCode\n");
        {
            Map<String, Set<String>> map = databaseModel.getDlcTabCodeMap();
            List<String> list =
                    map.entrySet().stream().map((x) -> x.getKey() + " - " + x.getValue()).sorted()
                            .toList();
            builder.append(gson.toJson(list)).append('\n');
        }
        builder.append('\n');

        view.setValidatorResultText(builder.toString());
    }

    @Override
    public void checkRemote() {
        if (!databaseModel.isLoaded()) {
            view.showErrorDialog("Database not loaded");
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
            builder.append("-------- conflict --------\n");
            builder.append(gson.toJson(databaseModel.conflict)).append('\n');
        }

        if (!databaseModel.unclassified.isEmpty()) {
            if (!builder.isEmpty()) {
                builder.append('\n');
            }

            builder.append("-------- unclassified --------\n");
            builder.append(gson.toJson(databaseModel.unclassified)).append('\n');
        }

        if (builder.isEmpty()) {
            builder.append("There are no conflicts or unclassified.\n");
        }

        view.setCheckerResultText(builder.toString());
    }
}
