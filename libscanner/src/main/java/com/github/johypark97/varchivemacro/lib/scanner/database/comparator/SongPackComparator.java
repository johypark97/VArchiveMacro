package com.github.johypark97.varchivemacro.lib.scanner.database.comparator;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import java.util.Comparator;

public class SongPackComparator implements Comparator<Song> {
    private final SongTitleComparator songTitleComparator = new SongTitleComparator();

    @Override
    public int compare(Song o1, Song o2) {
        int x = o1.pack().compareTo(o2.pack());
        if (x != 0) {
            return x;
        }

        return songTitleComparator.compare(o1, o2);
    }
}
