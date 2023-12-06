package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SongModel {
    void load(Path baseDir) throws IOException;

    boolean isLoaded();

    int getCount();

    LocalDlcSong getSong(int id);

    List<LocalDlcSong> getSongList();

    List<String> getDlcCodeList();

    Set<String> getDlcCodeSet();

    List<String> getDlcTabList();

    Set<String> getDlcTabSet();

    Map<String, Set<String>> getDlcTabCodeMap();

    TitleTool getTitleTool();
}
