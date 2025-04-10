package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.google.common.base.CharMatcher;

public interface TitleTool {
    char RIGHT_SINGLE_QUOTATION_MARK = '\u2019';

    static String normalizeTitle_training(String value) {
        return value.replace('l', 'I');
    }

    static String normalizeTitle_recognition(String value) {
        String s = normalizeTitle_training(value);
        s = CharMatcher.whitespace().removeFrom(s);
        s = s.replace(RIGHT_SINGLE_QUOTATION_MARK, '\'');

        return s;
    }

    boolean hasClippedTitle(Song song);

    String getClippedTitleOrDefault(Song song);

    String getRemoteTitleOrDefault(Song song);

    String remapScannedTitle(String value);
}
