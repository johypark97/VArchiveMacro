package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SongModel {
    void load(Path baseDir) throws IOException;

    boolean isLoaded();

    int getCount();

    LocalSong getSong(int id);

    List<LocalSong> getSongList();

    List<String> getDlcCodeList();

    Set<String> getDlcCodeSet();

    List<String> getDlcTabList();

    Set<String> getDlcTabSet();

    Map<String, Set<String>> getDlcTabCodeMap();

    String normalizeTitle(String value);

    boolean hasShortTitle(LocalSong song);

    String getShortTitle(LocalSong song);
}
