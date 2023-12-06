package com.github.johypark97.varchivemacro.lib.common.database;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DlcSongManager extends SongManager {
    LocalDlcSong getDlcSong(int id);

    List<LocalDlcSong> getDlcSongList();

    List<String> getDlcCodeList();

    Set<String> getDlcCodeSet();

    List<String> getDlcTabList();

    Set<String> getDlcTabSet();

    Map<String, String> getDlcCodeNameMap();

    Map<String, Set<String>> getDlcTabCodeMap();

    Map<String, List<LocalDlcSong>> getTabSongMap();

    class LocalDlcSong extends LocalSong {
        public final String dlc;
        public final String dlcTab;
        public final int dlcPriority;

        public LocalDlcSong(LocalSong song, String dlc, String dlcTab, int dlcPriority) {
            super(song.id, song.title, song.remoteTitle, song.composer, song.dlcCode,
                    song.priority);

            this.dlc = dlc;
            this.dlcPriority = dlcPriority;
            this.dlcTab = dlcTab;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }
    }
}
