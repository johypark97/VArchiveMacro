package com.github.johypark97.varchivemacro.lib.common.database.comparator;

import com.github.johypark97.varchivemacro.lib.common.database.SongManager.LocalSong;
import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class LocalSongComparator implements Comparator<LocalSong>, Serializable {
    @Serial
    private static final long serialVersionUID = -7774694683991236366L;

    private final TitleComparator titleComparator = new TitleComparator();

    @Override
    public int compare(LocalSong o1, LocalSong o2) {
        int ret = titleComparator.compare(o1.title, o2.title);
        if (ret != 0) {
            return ret;
        }

        return o1.priority - o2.priority;
    }
}
