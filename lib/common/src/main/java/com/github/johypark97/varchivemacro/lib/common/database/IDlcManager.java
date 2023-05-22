package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDlcManager {
    List<String> getDlcCodeList();

    Set<String> getDlcCodeSet();

    List<String> getDlcTabList();

    Set<String> getDlcTabSet();

    Map<String, String> getDlcCodeNameMap();

    Map<String, Set<String>> getDlcTabCodeMap();

    Map<String, List<LocalSong>> getTabSongMap();
}
