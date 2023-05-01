package com.github.johypark97.varchivemacro.macro.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DlcManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SongModel {
    private static final Path BASE_PATH = Path.of("data/database");

    private static final Path DLC_PATH = BASE_PATH.resolve("dlcs.json");
    private static final Path SONG_PATH = BASE_PATH.resolve("songs.json");
    private static final Path TAB_PATH = BASE_PATH.resolve("tabs.json");

    private final DlcManager dlcManager = new DlcManager(SONG_PATH, DLC_PATH, TAB_PATH);

    public SongModel() throws IOException {
    }

    public List<String> getTabs() {
        return dlcManager.getDlcTabList();
    }

    public Map<String, List<LocalSong>> getTabSongMap() {
        return dlcManager.getTabSongMap();
    }

    public Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs) {
        Map<String, List<LocalSong>> map = new LinkedHashMap<>();

        dlcManager.getTabSongMap().forEach(
                (key, value) -> map.put(key, selectedTabs.contains(key) ? value : List.of()));

        return map;
    }

    public Set<Integer> duplicateTitleSet() {
        return dlcManager.duplicateTitleSet();
    }
}
