package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Server;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.Event.Type;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskListModels.TaskListProvider;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultTaskManager implements TaskManager, Server<Event, TaskListProvider> {
    private final List<Client<Event, TaskListProvider>> clientList = new CopyOnWriteArrayList<>();
    private final Map<Integer, TaskData> taskDataMap = new ConcurrentHashMap<>();

    private ImageCacheManager imageCacheManager;

    @Override
    public Iterator<TaskData> iterator() {
        return taskDataMap.values().iterator();
    }

    @Override
    public void clear() {
        taskDataMap.clear();
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    @Override
    public synchronized TaskData createTask(LocalSong song, int songIndex, int songCount,
            CollectionArea collectionArea) {
        int taskNumber = taskDataMap.size();

        DefaultTaskData data =
                new DefaultTaskData(taskNumber, song, songIndex, songCount, collectionArea);
        taskDataMap.put(taskNumber, data);

        notifyClients(new Event(Type.ROWS_INSERTED, taskNumber));
        return data;
    }

    @Override
    public TaskData getTaskData(int taskNumber) {
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

    @Override
    public void addClient(Client<Event, TaskListProvider> client) {
        clientList.add(client);
        client.onAddClient(new TaskListProvider() {
            @Override
            public ResponseData getValue(int index) {
                TaskData task = taskDataMap.get(index);
                if (task == null) {
                    return null;
                }

                ResponseData data = new ResponseData();
                data.composer = task.getSong().composer();
                data.count = task.getSongCount();
                data.dlc = task.getSong().dlc();
                data.index = task.getSongIndex();
                data.status = task.getStatus();
                data.tab = task.getSong().dlcTab();
                data.taskNumber = task.getTaskNumber();
                data.title = task.getSong().title();

                return data;
            }

            @Override
            public int getCount() {
                return taskDataMap.size();
            }
        });
    }

    @Override
    public void notifyClients(Event data) {
        clientList.forEach((x) -> x.onNotify(data));
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
        private final LocalSong song;
        private final int songCount;
        private final int songIndex;
        private final int taskNumber;

        private Exception exception;
        private TaskStatus status = TaskStatus.NONE;

        public DefaultTaskData(int taskNumber, LocalSong song, int songIndex, int songCount,
                CollectionArea collectionArea) {
            this.collectionArea = collectionArea;
            this.song = song;
            this.songCount = songCount;
            this.songIndex = songIndex;
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
        public AnalyzedData getAnalyzedData(Button button, Pattern pattern) {
            return analyzedDataTable.get(button, pattern);
        }

        @Override
        public void addAnalyzedData(Button button, Pattern pattern, AnalyzedData data) {
            analyzedDataTable.put(button, pattern, data);
        }

        @Override
        public CollectionArea getCollectionArea() {
            return collectionArea;
        }

        @Override
        public LocalSong getSong() {
            return song;
        }

        @Override
        public int getSongCount() {
            return songCount;
        }

        @Override
        public int getSongIndex() {
            return songIndex;
        }

        @Override
        public int getTaskNumber() {
            return taskNumber;
        }

        @Override
        public Path getImagePath() {
            return imageCacheManager.createPath(song);
        }

        @Override
        public void saveImage(BufferedImage image) throws IOException {
            imageCacheManager.saveImage(song, image);
        }

        @Override
        public BufferedImage loadImage() throws IOException {
            return imageCacheManager.loadImage(song);
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
        public TaskStatus getStatus() {
            return status;
        }

        @Override
        public void setStatus(TaskStatus value) {
            status = value;
            notifyClients(new Event(Type.ROWS_UPDATED, taskNumber));
        }
    }
}
