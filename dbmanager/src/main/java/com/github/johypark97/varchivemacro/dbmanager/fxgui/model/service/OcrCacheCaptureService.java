package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheCaptureTask;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import java.util.List;
import java.util.Objects;

public class OcrCacheCaptureService extends BaseService<OcrCacheCaptureTask, Void> {
    private List<LocalDlcSong> dlcSongList;

    @Override
    protected OcrCacheCaptureTask newTask() {
        OcrCacheCaptureTask task = super.newTask();

        task.dlcSongList = dlcSongList;

        return task;
    }

    public static class Builder extends BaseService.Builder<Builder, OcrCacheCaptureService> {
        private List<LocalDlcSong> dlcSongList;

        public Builder() {
            super(OcrCacheCaptureService.class);
        }

        public Builder setDlcSongList(List<LocalDlcSong> value) {
            dlcSongList = List.copyOf(value);
            return this;
        }

        @Override
        public void build() {
            Objects.requireNonNull(dlcSongList);

            super.build();
            OcrCacheCaptureService service =
                    ServiceManager.getInstance().get(OcrCacheCaptureService.class);

            service.dlcSongList = dlcSongList;
        }

        @Override
        protected Builder getInstance() {
            return this;
        }
    }
}
