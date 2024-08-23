package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheCaptureTask;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import java.util.List;
import java.util.Objects;

public class OcrCacheCaptureService extends BaseService<OcrCacheCaptureTask, Void> {
    private List<Song> songList;

    @Override
    protected OcrCacheCaptureTask newTask() {
        OcrCacheCaptureTask task = super.newTask();

        task.songList = songList;

        return task;
    }

    public static class Builder extends BaseService.Builder<Builder, OcrCacheCaptureService> {
        private List<Song> songList;

        public Builder() {
            super(OcrCacheCaptureService.class);
        }

        public Builder setSongList(List<Song> value) {
            songList = List.copyOf(value);
            return this;
        }

        @Override
        public void build() {
            Objects.requireNonNull(songList);

            super.build();
            OcrCacheCaptureService service =
                    ServiceManager.getInstance().get(OcrCacheCaptureService.class);

            service.songList = songList;
        }

        @Override
        protected Builder getInstance() {
            return this;
        }
    }
}
