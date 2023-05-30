package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface TaskManager extends Iterable<TaskData> {
    void clear();

    TaskData createTask(LocalSong song, int songIndex, int songCount,
            CollectionArea collectionArea);

    TaskData getTaskData(int taskNumber);

    ImageCacheManager getImageCacheManager();

    void setImageCacheManager(ImageCacheManager imageCacheManager);

    enum TaskStatus {
        ANALYZED, ANALYZING, CACHED, CAPTURED, EXCEPTION, NONE, WAITING
    }


    interface TaskData extends Iterable<Cell<Button, Pattern, AnalyzedData>> {
        void clearAnalyzedData();

        AnalyzedData getAnalyzedData(Button button, Pattern pattern);

        void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data);

        CollectionArea getCollectionArea();

        LocalSong getSong();

        int getSongCount();

        int getSongIndex();

        int getTaskNumber();

        Path getImagePath();

        void saveImage(BufferedImage image) throws IOException;

        BufferedImage loadImage() throws IOException;

        Exception getException();

        void setException(Exception value);

        TaskStatus getStatus();

        void setStatus(TaskStatus value);
    }


    interface AnalyzedData {
        String getRateText();

        void setRateText(String value);

        boolean isMaxCombo();

        void setMaxCombo(boolean value);
    }
}
