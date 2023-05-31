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
    void clearTask();

    TaskData createTask(LocalSong song, CollectionArea collectionArea);

    TaskData getTask(int taskNumber);

    ImageCacheManager getImageCacheManager();

    void setImageCacheManager(ImageCacheManager imageCacheManager);

    enum TaskStatus {
        ANALYZED, ANALYZING, CACHED, CAPTURED, EXCEPTION, NONE, WAITING
    }


    interface TaskData extends Iterable<Cell<Button, Pattern, AnalyzedData>> {
        void clearAnalyzedData();

        void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data);

        AnalyzedData getAnalyzedData(Button button, Pattern pattern);

        int getTaskNumber();

        LocalSong getSong();

        CollectionArea getCollectionArea();

        Path getImagePath();

        void saveImage(BufferedImage image) throws IOException;

        BufferedImage loadImage() throws IOException;

        Exception getException();

        void setException(Exception value);

        int getSongCount();

        void setSongCount(int value);

        int getSongIndex();

        void setSongIndex(int value);

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
