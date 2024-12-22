package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabaseModel {
    void load() throws IOException, SQLException;

    TitleTool getTitleTool();

    Song getSong(int id);

    List<String> categoryNameList();

    Map<String, List<Song>> categoryNameSongListMap();

    Set<Integer> duplicateTitleSongIdSet();

    String getRemoteTitle(int id);
}
