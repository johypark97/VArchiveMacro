package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data;

import com.github.johypark97.varchivemacro.lib.common.database.SongManager.LocalSong;
import com.google.common.base.CaseFormat;
import java.util.Map;

public class SongData {
    private final LocalSong song;
    private final String dlcName;

    public SongData(LocalSong song, Map<String, String> dlcCodeNameMap) {
        this.song = song;

        dlcName = dlcCodeNameMap.get(song.dlcCode);
    }

    public int getId() {
        return song.id;
    }

    public String getTitle() {
        return song.title;
    }

    public String getRemoteTitle() {
        return (song.remoteTitle != null) ? song.remoteTitle : "";
    }

    public String getComposer() {
        return song.composer;
    }

    public String getDlc() {
        return dlcName;
    }

    public int getPriority() {
        return song.priority;
    }

    public enum SongProperty {
        ID, TITLE, REMOTE_TITLE, COMPOSER, DLC, PRIORITY;

        @Override
        public String toString() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, super.toString());
        }
    }
}
