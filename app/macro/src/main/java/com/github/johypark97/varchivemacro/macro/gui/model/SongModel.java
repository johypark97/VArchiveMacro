package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DlcManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SongModel {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.dir"), "data/database");

    private static final Path DLC_PATH = BASE_PATH.resolve("dlcs.json");
    private static final Path SONG_PATH = BASE_PATH.resolve("songs.json");
    private static final Path TAB_PATH = BASE_PATH.resolve("tabs.json");
    private static final Path UNLOCK_PATH = BASE_PATH.resolve("unlocks.json");

    private final DlcManager dlcManager =
            new DlcManager(SONG_PATH, DLC_PATH, TAB_PATH, UNLOCK_PATH);

    public SongModel() throws IOException {
    }

    public Map<String, String> getDlcCodeNameMap() {
        return dlcManager.getDlcCodeNameMap();
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        return dlcManager.getTabSongMap();
    }

    public Map<String, List<LocalSong>> getTabSongMap(Set<String> ownedDlcs) {
        return dlcManager.getTabSongMap(ownedDlcs);
    }
}
