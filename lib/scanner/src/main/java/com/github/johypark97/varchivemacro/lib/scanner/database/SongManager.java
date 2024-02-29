package com.github.johypark97.varchivemacro.lib.scanner.database;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public interface SongManager {
    int getCount();

    LocalSong getSong(int id);

    List<LocalSong> getSongList();

    Set<Integer> getDuplicateTitleSet();

    class LocalSong {
        public final String composer;
        public final String dlcCode;
        public final String remoteTitle;
        public final String title;
        public final int id;
        public final int priority;

        public LocalSong(int id, String title, String remoteTitle, String composer, String dlcCode,
                int priority) {
            this.composer = composer;
            this.dlcCode = dlcCode;
            this.id = id;
            this.priority = priority;
            this.remoteTitle = remoteTitle;
            this.title = title;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            LocalSong song = (LocalSong) obj;
            return id == song.id;
        }
    }
}
