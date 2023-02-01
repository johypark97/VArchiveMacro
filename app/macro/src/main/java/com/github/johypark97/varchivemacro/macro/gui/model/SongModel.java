package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.SongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SongModel {
    private static final Path BASE_PATH = Path.of(System.getProperty("user.dir"), "data/database");

    private static final Path DLC_DATA_PATH = BASE_PATH.resolve("dlcdata.json");
    private static final Path SONG_PATH = BASE_PATH.resolve("songs.json");

    private final SongManager songManager = new SongManager(SONG_PATH, DLC_DATA_PATH);

    public SongModel() throws IOException {
    }

    public Map<String, String> getDlcCodeNameMap() {
        return songManager.getDlcCodeNameMap();
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        return songManager.getTabSongMap();
    }

    public Map<String, List<LocalSong>> getTabSongMap(Set<String> ownedDlcs) {
        return songManager.getTabSongMap(ownedDlcs);
    }
}
