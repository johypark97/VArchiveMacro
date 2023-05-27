package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISongRecordManager extends DlcSongManager, RecordManager {
    Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs);
}
