package com.github.johypark97.varchivemacro.lib.common.database.comparator;

import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class DlcLocalSongComparator implements Comparator<LocalDlcSong>, Serializable {
    @Serial
    private static final long serialVersionUID = 4332389879534930550L;

    private final LocalSongComparator localSongComparator = new LocalSongComparator();

    @Override
    public int compare(LocalDlcSong o1, LocalDlcSong o2) {
        int ret = o1.dlcPriority - o2.dlcPriority;
        if (ret != 0) {
            return ret;
        }

        return localSongComparator.compare(o1, o2);
    }
}
