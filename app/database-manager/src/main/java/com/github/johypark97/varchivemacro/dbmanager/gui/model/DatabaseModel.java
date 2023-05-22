package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableRowSorter;
import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher.RemoteSong;
import com.github.johypark97.varchivemacro.lib.common.database.DlcManager;
import com.github.johypark97.varchivemacro.lib.common.database.SongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatabaseModel {
    private static final String DLC_FILENAME = "dlcs.json";
    private static final String SONG_FILENAME = "songs.json";
    private static final String TAB_FILENAME = "tabs.json";

    private final DlcManager dlcManager = new DlcManager();
    private final SongManager songManager = new SongManager();
    private boolean isManagerloaded;

    public DatabaseTableModel tableModel;
    public DatabaseTableRowSorter tableRowSorter;

    public List<RemoteSong> conflict;
    public List<RemoteSong> unclassified;

    public void load(Path baseDir) throws IOException {
        Path dlcPath = baseDir.resolve(DLC_FILENAME);
        Path songPath = baseDir.resolve(SONG_FILENAME);
        Path tabPath = baseDir.resolve(TAB_FILENAME);

        songManager.load(songPath);

        dlcManager.load(dlcPath, tabPath);
        dlcManager.setSongManager(songManager);

        tableModel = new DatabaseTableModel(songManager);
        tableRowSorter = new DatabaseTableRowSorter(tableModel);

        isManagerloaded = true;
    }

    public boolean isLoaded() {
        return isManagerloaded;
    }

    public List<String> getFilterableColumns() {
        return DatabaseTableModel.COLUMNS.stream().filter((x) -> !"id".equals(x)).toList();
    }

    public void setFilter(String pattern, String column) {
        if (tableRowSorter != null) {
            tableRowSorter.setFilter(pattern, column);
        }
    }

    public List<LocalSong> getSongs() {
        return songManager.getSongList();
    }

    public List<String> getDlcCodeList() {
        return dlcManager.getDlcCodeList();
    }

    public Set<String> getDlcCodeSet() {
        return dlcManager.getDlcCodeSet();
    }

    public List<String> getDlcTabList() {
        return dlcManager.getDlcTabList();
    }

    public Set<String> getDlcTabSet() {
        return dlcManager.getDlcTabSet();
    }

    public Map<String, Set<String>> getDlcTabCodeMap() {
        return dlcManager.getDlcTabCodeMap();
    }

    public void checkRemote() throws GeneralSecurityException, IOException, InterruptedException {
        StaticFetcher staticFetcher = Api.newStaticFetcher();

        conflict = new ArrayList<>();
        unclassified = new ArrayList<>();

        staticFetcher.fetchSongs();
        for (RemoteSong remoteSong : staticFetcher.getSongs()) {
            LocalSong localSong = songManager.getSong(remoteSong.id);
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

        return localSong.dlcCode().equals(remoteSong.dlcCode);
    }
}
