package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DefaultDlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultSongModel implements SongModel {
    private static final String DLC_FILENAME = "dlcs.json";
    private static final String SONG_FILENAME = "songs.json";
    private static final String TAB_FILENAME = "tabs.json";
    private static final String TITLES_FILENAME = "titles.json";

    private DlcSongManager dlcSongManager;
    private TitleTool titleTool;

    @Override
    public void load(Path baseDir) throws IOException {
        Path dlcPath = baseDir.resolve(DLC_FILENAME);
        Path songPath = baseDir.resolve(SONG_FILENAME);
        Path tabPath = baseDir.resolve(TAB_FILENAME);
        Path titlesPath = baseDir.resolve(TITLES_FILENAME);

        dlcSongManager = new DefaultDlcSongManager(songPath, dlcPath, tabPath);
        titleTool = new DefaultTitleTool(titlesPath);
    }

    @Override
    public boolean isLoaded() {
        return dlcSongManager != null;
    }

    @Override
    public int getCount() {
        return dlcSongManager.getCount();
    }

    @Override
    public LocalSong getSong(int id) {
        return dlcSongManager.getSong(id);
    }

    @Override
    public List<LocalSong> getSongList() {
        return dlcSongManager.getSongList();
    }

    @Override
    public List<String> getDlcCodeList() {
        return dlcSongManager.getDlcCodeList();
    }

    @Override
    public Set<String> getDlcCodeSet() {
        return dlcSongManager.getDlcCodeSet();
    }

    @Override
    public List<String> getDlcTabList() {
        return dlcSongManager.getDlcTabList();
    }

    @Override
    public Set<String> getDlcTabSet() {
        return dlcSongManager.getDlcTabSet();
    }

    @Override
    public Map<String, Set<String>> getDlcTabCodeMap() {
        return dlcSongManager.getDlcTabCodeMap();
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }
}
