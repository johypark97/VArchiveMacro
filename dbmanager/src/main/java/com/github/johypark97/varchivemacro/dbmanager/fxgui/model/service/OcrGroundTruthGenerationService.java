package com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.service.task.OcrGroundTruthGenerationTask;
import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class OcrGroundTruthGenerationService
        extends BaseService<OcrGroundTruthGenerationTask, Void> {
    private List<Song> songList;
    private TitleTool titleTool;

    @Override
    protected OcrGroundTruthGenerationTask newTask() {
        OcrGroundTruthGenerationTask task = super.newTask();

        task.songList = songList;
        task.titleTool = titleTool;

        return task;
    }

    public static class Builder
            extends BaseService.Builder<Builder, OcrGroundTruthGenerationService> {
        private Consumer<Double> onUpdateProgress;
        private List<Song> songList;
        private TitleTool titleTool;

        public Builder() {
            super(OcrGroundTruthGenerationService.class);
        }

        public Builder setSongList(List<Song> value) {
            songList = List.copyOf(value);
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
            Objects.requireNonNull(onUpdateProgress);
            Objects.requireNonNull(songList);
            Objects.requireNonNull(titleTool);

            super.build();
            OcrGroundTruthGenerationService service =
                    ServiceManager.getInstance().get(OcrGroundTruthGenerationService.class);

            service.songList = songList;
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
