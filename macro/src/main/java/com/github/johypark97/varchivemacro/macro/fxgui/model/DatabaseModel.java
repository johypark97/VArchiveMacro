package com.github.johypark97.varchivemacro.macro.fxgui.model;

import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DatabaseModel {
    void load() throws IOException;

    Map<String, List<LocalDlcSong>> getDlcTapSongMap();
}