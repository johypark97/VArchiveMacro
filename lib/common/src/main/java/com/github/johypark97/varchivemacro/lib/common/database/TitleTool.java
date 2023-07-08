package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;

public interface TitleTool {
    String getTitleChars();

    String normalizeTitle(String value);

    boolean hasShortTitle(LocalSong song);

    String getShortTitle(LocalSong song);
}
