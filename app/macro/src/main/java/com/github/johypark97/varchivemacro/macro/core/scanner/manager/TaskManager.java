package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface TaskManager extends Iterable<TaskData> {
    void clearTask();

    TaskData createTask(CollectionArea collectionArea);

    TaskData getTask(int taskNumber);

    ImageCacheManager getImageCacheManager();

    void setImageCacheManager(ImageCacheManager imageCacheManager);

    enum TaskStatus {
        ANALYZED, ANALYZING, DUPLICATED, EXCEPTION, FOUND, NONE, NOT_FOUND, WAITING
    }


    interface TaskData extends Iterable<Cell<Button, Pattern, AnalyzedData>> {
        void clearAnalyzedData();

        void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data);

        AnalyzedData getAnalyzedData(Button button, Pattern pattern);

        int getTaskNumber();

        CollectionArea getCollectionArea();

        Path getImagePath();

        void saveImage(BufferedImage image) throws IOException;

        BufferedImage loadImage() throws IOException;

        float getAccuracy();

        void setAccuracy(float value);

        int getDistance();

        void setDistance(int value);

        boolean hasException();

        Exception getException();

        void setException(Exception value);

        String getScannedTitle();

        void setScannedTitle(String value);

        boolean isSelected();

        void setSelected(boolean value);

        LocalSong getSong();

        void setSong(LocalSong value);

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
