package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.database.IDlcManager;
import com.github.johypark97.varchivemacro.lib.common.database.IRecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.ISongManager;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISongRecordManager extends IDlcManager, IRecordManager, ISongManager {
    Map<String, List<LocalSong>> getTabSongMap(Set<String> selectedTabs);
}
