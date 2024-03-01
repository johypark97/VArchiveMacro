package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service;

import com.github.johypark97.varchivemacro.dbmanager.core.ServiceManager;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrCacheClassificationTask;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class OcrCacheClassificationService extends BaseService<OcrCacheClassificationTask, Void> {
    private List<LocalDlcSong> dlcSongList;
    private TitleTool titleTool;

    @Override
    protected OcrCacheClassificationTask newTask() {
        OcrCacheClassificationTask task = super.newTask();

        task.dlcSongList = dlcSongList;
        task.titleTool = titleTool;

        return task;
    }

    public static class Builder
            extends BaseService.Builder<Builder, OcrCacheClassificationService> {
        private Consumer<Double> onUpdateProgress;
        private List<LocalDlcSong> dlcSongList;
        private TitleTool titleTool;

        public Builder() {
            super(OcrCacheClassificationService.class);
        }

        public Builder setDlcSongList(List<LocalDlcSong> value) {
            dlcSongList = List.copyOf(value);
            return this;
        }

        public Builder setTitleTool(TitleTool value) {
            titleTool = value;
            return this;
        }

        public Builder setOnUpdateProgress(Consumer<Double> value) {
            onUpdateProgress = value;
            return this;
        }

        @Override
        public void build() {
            Objects.requireNonNull(dlcSongList);
            Objects.requireNonNull(onUpdateProgress);
            Objects.requireNonNull(titleTool);

            super.build();
            OcrCacheClassificationService service =
                    ServiceManager.getInstance().get(OcrCacheClassificationService.class);

            service.dlcSongList = dlcSongList;
            service.titleTool = titleTool;

            service.progressProperty().addListener(
                    (observable, oldValue, newValue) -> onUpdateProgress.accept(
                            newValue.doubleValue()));
        }

        @Override
        protected Builder getInstance() {
            return this;
        }
    }
}
