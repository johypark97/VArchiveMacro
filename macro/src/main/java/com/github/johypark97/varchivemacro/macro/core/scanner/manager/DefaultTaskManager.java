package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.scanner.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.TaskListProvider;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTaskManager implements TaskManager, TaskListProvider {
    private final Map<Integer, TaskData> taskDataMap = new ConcurrentHashMap<>();

    private ImageCacheManager imageCacheManager;

    public TaskListProvider getTaskListProvider() {
        return this;
    }

    @Override
    public Iterator<TaskData> iterator() {
        return taskDataMap.values().iterator();
    }

    @Override
    public ResponseData getValue(int index) {
        TaskData task = taskDataMap.get(index);
        if (task == null) {
            return null;
        }

        ResponseData data = new ResponseData();

        data.accuracy = task.getAccuracy();
        data.distance = task.getDistance();
        data.scannedTitle = task.getScannedTitle();
        data.selected = task.isSelected();
        data.status = task.getStatus();
        data.taskNumber = task.getTaskNumber();

        if (task.getSong() != null) {
            data.composer = task.getSong().composer;
            data.dlc = task.getSong().dlc;
            data.tab = task.getSong().dlcTab;
            data.title = task.getSong().title;
        }

        return data;
    }

    @Override
    public int getCount() {
        return taskDataMap.size();
    }

    @Override
    public void updateSelected(int index, boolean value) {
        TaskData task = taskDataMap.get(index);
        if (task != null) {
            task.setSelected(value);
        }
    }

    @Override
    public void clearTask() {
        taskDataMap.clear();
    }

    @Override
    public synchronized TaskData createTask(CollectionArea collectionArea) {
        int taskNumber = taskDataMap.size();

        DefaultTaskData data = new DefaultTaskData(taskNumber, collectionArea);
        taskDataMap.put(taskNumber, data);

        return data;
    }

    @Override
    public TaskData getTask(int taskNumber) {
        return taskDataMap.get(taskNumber);
    }

    @Override
    public ImageCacheManager getImageCacheManager() {
        return imageCacheManager;
    }

    @Override
    public void setImageCacheManager(ImageCacheManager imageCacheManager) {
        this.imageCacheManager = imageCacheManager;
    }

    public static class DefaultAnalyzedData implements AnalyzedData {
        private String rateText;
        private boolean maxCombo;

        @Override
        public String getRateText() {
            return rateText;
        }

        @Override
        public void setRateText(String value) {
            rateText = value;
        }

        @Override
        public boolean isMaxCombo() {
            return maxCombo;
        }

        @Override
        public void setMaxCombo(boolean value) {
            maxCombo = value;
        }
    }


    private class DefaultTaskData implements TaskData {
        private final Table<Button, Pattern, AnalyzedData> analyzedDataTable =
                HashBasedTable.create();

        private final CollectionArea collectionArea;
        private final int taskNumber;

        private LocalDlcSong song;
        private Exception exception;
        private String scannedTitle = "";
        private TaskStatus status = TaskStatus.NONE;
        private boolean selected;
        private float accuracy;
        private int distance;

        public DefaultTaskData(int taskNumber, CollectionArea collectionArea) {
            this.collectionArea = collectionArea;
            this.taskNumber = taskNumber;
        }

        @Override
        public Iterator<Cell<Button, Pattern, AnalyzedData>> iterator() {
            return analyzedDataTable.cellSet().iterator();
        }

        @Override
        public void clearAnalyzedData() {
            analyzedDataTable.clear();
        }

        @Override
        public void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data) {
            analyzedDataTable.put(button, pattern, data);
        }

        @Override
        public AnalyzedData getAnalyzedData(Button button, Pattern pattern) {
            return analyzedDataTable.get(button, pattern);
        }

        @Override
        public int getTaskNumber() {
            return taskNumber;
        }

        @Override
        public CollectionArea getCollectionArea() {
            return collectionArea;
        }

        @Override
        public Path getImagePath() {
            return imageCacheManager.createPath(taskNumber);
        }

        @Override
        public void saveImage(BufferedImage image) throws IOException {
            imageCacheManager.saveImage(taskNumber, image);
        }

        @Override
        public BufferedImage loadImage() throws IOException {
            return imageCacheManager.loadImage(taskNumber);
        }

        @Override
        public float getAccuracy() {
            return accuracy;
        }

        @Override
        public void setAccuracy(float value) {
            accuracy = value;
        }

        @Override
        public int getDistance() {
            return distance;
        }

        @Override
        public void setDistance(int value) {
            distance = value;
        }

        @Override
        public boolean hasException() {
            return exception != null;
        }

        @Override
        public Exception getException() {
            return exception;
        }

        @Override
        public void setException(Exception value) {
            exception = value;
            setStatus(TaskStatus.EXCEPTION);
        }

        @Override
        public String getScannedTitle() {
            return scannedTitle;
        }

        @Override
        public void setScannedTitle(String value) {
            scannedTitle = value;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean value) {
            selected = value;
        }

        @Override
        public LocalDlcSong getSong() {
            return song;
        }

        @Override
        public void setSong(LocalDlcSong value) {
            song = value;
        }

        @Override
        public TaskStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(TaskStatus value) {
            status = value;
        }
    }
}
