package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.model.datastruct.DatabaseTableRowSorter;
import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher.RemoteSong;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
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

    public final List<RemoteSong> conflictList = new ArrayList<>();
    public final List<RemoteSong> unclassifiedList = new ArrayList<>();

    private DlcSongManager dlcSongManager;
    private boolean isManagerLoaded;

    public DatabaseTableModel tableModel;
    public DatabaseTableRowSorter tableRowSorter;

    public void load(Path baseDir) throws IOException {
        Path dlcPath = baseDir.resolve(DLC_FILENAME);
        Path songPath = baseDir.resolve(SONG_FILENAME);
        Path tabPath = baseDir.resolve(TAB_FILENAME);

        dlcSongManager = new DefaultDlcSongManager(songPath, dlcPath, tabPath);

        tableModel = new DatabaseTableModel(dlcSongManager);
        tableRowSorter = new DatabaseTableRowSorter(tableModel);

        isManagerLoaded = true;
    }

    public boolean isLoaded() {
        return isManagerLoaded;
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
        return dlcSongManager.getSongList();
    }

    public List<String> getDlcCodeList() {
        return dlcSongManager.getDlcCodeList();
    }

    public Set<String> getDlcCodeSet() {
        return dlcSongManager.getDlcCodeSet();
    }

    public List<String> getDlcTabList() {
        return dlcSongManager.getDlcTabList();
    }

    public Set<String> getDlcTabSet() {
        return dlcSongManager.getDlcTabSet();
    }

    public Map<String, Set<String>> getDlcTabCodeMap() {
        return dlcSongManager.getDlcTabCodeMap();
    }

    public void checkRemote() throws GeneralSecurityException, IOException, InterruptedException {
        StaticFetcher staticFetcher = Api.newStaticFetcher();

        conflictList.clear();
        unclassifiedList.clear();

        staticFetcher.fetchSongs();
        for (RemoteSong remoteSong : staticFetcher.getSongs()) {
            LocalSong localSong = dlcSongManager.getSong(remoteSong.id);
            if (localSong == null) {
                unclassifiedList.add(remoteSong);
            } else if (!compareSong(localSong, remoteSong)) {
                conflictList.add(remoteSong);
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
