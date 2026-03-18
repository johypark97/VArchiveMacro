package com.github.johypark97.varchivemacro.lib.scanner.database.comparator;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import java.util.Comparator;

public class SongTitleComparator implements Comparator<Song> {
    private final TitleComparator titleComparator = new TitleComparator();

    @Override
    public int compare(Song o1, Song o2) {
        int x = titleComparator.compare(o1.title(), o2.title());
        if (x != 0) {
            return x;
        }

        return o1.priority() - o2.priority();
    }
}
