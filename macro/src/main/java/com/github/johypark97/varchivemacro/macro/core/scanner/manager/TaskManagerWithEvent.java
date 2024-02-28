package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.area.CollectionArea;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.protocol.Observers.Observable;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Event.Type;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class TaskManagerWithEvent implements TaskManager {
    public final Observable<Event> observable;
    public final TaskManager decorated;

    public TaskManagerWithEvent(TaskManager decorated, Observable<Event> observable) {
        this.decorated = decorated;
        this.observable = observable;
    }

    @Override
    public Iterator<TaskData> iterator() {
        return new Iterator<>() {
            private final Iterator<TaskData> iterator = decorated.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public TaskData next() {
                return new TaskDataWithEvent(iterator.next());
            }
        };
    }

    @Override
    public void clearTask() {
        decorated.clearTask();
    }

    @Override
    public TaskData createTask(CollectionArea collectionArea) {
        return new TaskDataWithEvent(decorated.createTask(collectionArea));
    }

    @Override
    public TaskData getTask(int taskNumber) {
        return new TaskDataWithEvent(decorated.getTask(taskNumber));
    }

    @Override
    public ImageCacheManager getImageCacheManager() {
        return decorated.getImageCacheManager();
    }

    @Override
    public void setImageCacheManager(ImageCacheManager imageCacheManager) {
        decorated.setImageCacheManager(imageCacheManager);
    }

    private class TaskDataWithEvent implements TaskData {
        private final TaskData decorated;

        public TaskDataWithEvent(TaskData decorated) {
            this.decorated = decorated;
        }

        private void notifyRowsUpdated() {
            observable.notifyObservers(new Event(Type.ROWS_UPDATED, decorated.getTaskNumber()));
        }

        @Override
        public Iterator<Cell<Button, Pattern, AnalyzedData>> iterator() {
            return decorated.iterator();
        }

        @Override
        public void clearAnalyzedData() {
            decorated.clearAnalyzedData();
        }

        @Override
        public void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data) {
            decorated.addAnalyzedData(button, pattern, data);
        }

        @Override
        public AnalyzedData getAnalyzedData(Button button, Pattern pattern) {
            return decorated.getAnalyzedData(button, pattern);
        }

        @Override
        public int getTaskNumber() {
            return decorated.getTaskNumber();
        }

        @Override
        public CollectionArea getCollectionArea() {
            return decorated.getCollectionArea();
        }

        @Override
        public Path getImagePath() {
            return decorated.getImagePath();
        }

        @Override
        public void saveImage(BufferedImage image) throws IOException {
            decorated.saveImage(image);
        }

        @Override
        public BufferedImage loadImage() throws IOException {
            return decorated.loadImage();
        }

        @Override
        public float getAccuracy() {
            return decorated.getAccuracy();
        }

        @Override
        public void setAccuracy(float value) {
            decorated.setAccuracy(value);
        }

        @Override
        public int getDistance() {
            return decorated.getDistance();
        }

        @Override
        public void setDistance(int value) {
            decorated.setDistance(value);
        }

        @Override
        public boolean hasException() {
            return decorated.hasException();
        }

        @Override
        public Exception getException() {
            return decorated.getException();
        }

        @Override
        public void setException(Exception value) {
            decorated.setException(value);
            notifyRowsUpdated();
        }

        @Override
        public String getScannedTitle() {
            return decorated.getScannedTitle();
        }

        @Override
        public void setScannedTitle(String value) {
            decorated.setScannedTitle(value);
        }

        @Override
        public boolean isSelected() {
            return decorated.isSelected();
        }

        @Override
        public void setSelected(boolean value) {
            decorated.setSelected(value);
        }

        @Override
        public LocalDlcSong getSong() {
            return decorated.getSong();
        }

        @Override
        public void setSong(LocalDlcSong value) {
            decorated.setSong(value);
        }

        @Override
        public TaskStatus getStatus() {
            return decorated.getStatus();
        }

        @Override
        public void setStatus(TaskStatus value) {
            decorated.setStatus(value);
            notifyRowsUpdated();
        }
    }
}
