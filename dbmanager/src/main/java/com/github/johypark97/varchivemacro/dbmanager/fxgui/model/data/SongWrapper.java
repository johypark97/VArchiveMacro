package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.google.common.base.CaseFormat;

public class SongWrapper {
    private final Song song;

    public SongWrapper(Song song) {
        this.song = song;
    }

    public int getId() {
        return song.id();
    }

    public String getTitle() {
        return song.title();
    }

    public String getComposer() {
        return song.composer();
    }

    public String getPack() {
        return song.pack().name();
    }

    public String getCategory() {
        return song.pack().category().name();
    }

    public int getCategoryPriority() {
        return song.pack().category().priority();
    }

    public int getPackPriority() {
        return song.pack().priority();
    }

    public int getTitlePriority() {
        return song.priority();
    }

    public enum SongDataProperty {
        ID,
        TITLE,
        COMPOSER,
        PACK,
        CATEGORY,
        PRIORITY_CATEGORY,
        PRIORITY_PACK,
        PRIORITY_TITLE;

        @Override
        public String toString() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, super.toString());
        }
    }
}
