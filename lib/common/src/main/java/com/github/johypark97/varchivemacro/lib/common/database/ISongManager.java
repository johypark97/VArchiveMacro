package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.Set;

public interface ISongManager {
    int getCount();

    LocalSong getSong(int id);

    List<LocalSong> getSongList();

    Set<Integer> getDuplicateTitleSet();
}
