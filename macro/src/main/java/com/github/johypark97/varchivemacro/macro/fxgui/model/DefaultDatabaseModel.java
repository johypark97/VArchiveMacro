package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class DefaultDatabaseModel implements DatabaseModel {
    private static final Path BASE_PATH = Path.of("data/database");

    private static final Path DLC_PATH = BASE_PATH.resolve("dlcs.json");
    private static final Path SONG_PATH = BASE_PATH.resolve("songs.json");
    private static final Path TAB_PATH = BASE_PATH.resolve("tabs.json");
    private static final Path TITLE_PATH = BASE_PATH.resolve("titles.json");

    private DlcSongManager dlcSongManager;
    private TitleTool titleTool;

    @Override
    public void load() throws IOException {
        dlcSongManager = new DefaultDlcSongManager(SONG_PATH, DLC_PATH, TAB_PATH);
        titleTool = new DefaultTitleTool(TITLE_PATH);
    }

    @Override
    public Map<String, List<LocalDlcSong>> getDlcTapSongMap() {
        return dlcSongManager.getTabSongMap();
    }

    @Override
    public LocalDlcSong getDlcSong(int id) {
        return dlcSongManager.getDlcSong(id);
    }

    @Override
    public List<String> getDlcTabList() {
        return dlcSongManager.getDlcTabList();
    }

    @Override
    public List<LocalDlcSong> getDlcSongList() {
        return dlcSongManager.getDlcSongList();
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }
}
