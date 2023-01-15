package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableRowSorter;
import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.datastruct.staticfetcher.RemoteSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseModel {
    public Map<Integer, LocalSong> songs;

    public List<RemoteSong> conflict;
    public List<RemoteSong> unclassified;

    public DatabaseTableModel tableModel;
    public DatabaseTableRowSorter tableRowSorter;

    public void loadSongs(Path path) throws IOException {
        songs = LocalSong.loadJson(path);

        tableModel = new DatabaseTableModel(this);
        tableRowSorter = new DatabaseTableRowSorter(tableModel);
    }

    public boolean isSongLoaded() {
        return songs != null;
    }

    public String[] getFilterableColumns() {
        return DatabaseTableModel.COLUMNS.stream().filter((x) -> !"id".equals(x))
                .toArray(String[]::new);
    }

    public void setFilter(String pattern, String column) {
        if (tableRowSorter != null) {
            tableRowSorter.setFilter(pattern, column);
        }
    }

    public void checkRemote() throws GeneralSecurityException, IOException, InterruptedException {
        StaticFetcher staticFetcher = Api.newStaticFetcher();

        conflict = new ArrayList<>();
        unclassified = new ArrayList<>();

        staticFetcher.fetchSongs();
        for (RemoteSong remoteSong : staticFetcher.getSongs()) {
            LocalSong localSong = songs.get(remoteSong.id);
            if (localSong == null) {
                unclassified.add(remoteSong);
            } else if (!compareSong(localSong, remoteSong)) {
                conflict.add(remoteSong);
            }
        }
    }

    private boolean compareSong(LocalSong localSong, RemoteSong remoteSong) {
        if (localSong.title().equals(remoteSong.title)) {
            if (localSong.remote_title() != null) {
                return false;
            }
        } else {
            if (localSong.remote_title() == null || !localSong.remote_title()
                    .equals(remoteSong.title)) {
                return false;
            }
        }

        return localSong.dlc().equals(remoteSong.dlc) && localSong.dlcCode()
                .equals(remoteSong.dlcCode);
    }
}
