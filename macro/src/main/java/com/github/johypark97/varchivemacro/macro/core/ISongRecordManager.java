package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISongRecordManager extends DlcSongManager {
    RecordManager getRecordManager();

    Map<String, List<LocalDlcSong>> getTabSongMap(Set<String> selectedTabs);

    TitleTool getTitleTool();
}
