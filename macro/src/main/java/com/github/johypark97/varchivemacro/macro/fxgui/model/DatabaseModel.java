package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabaseModel {
    void load() throws IOException;

    Map<String, List<LocalDlcSong>> getDlcTapSongMap();

    LocalDlcSong getDlcSong(int id);

    List<String> getDlcTabList();

    TitleTool getTitleTool();

    Set<Integer> getDuplicateTitleSet();
}
