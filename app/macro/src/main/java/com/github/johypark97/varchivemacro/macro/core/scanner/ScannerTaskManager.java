package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Server;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModel.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModel.Event.Type;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModel.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModel.TaskServer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

class ScannerTaskManager implements Server<Event, TaskServer> {
    private final List<Client<Event, TaskServer>> clientList = new CopyOnWriteArrayList<>();
    private final Map<Integer, ScannerTask> tasks = new ConcurrentHashMap<>();

    private Path cacheDir = Path.of("");

    public void setCacheDir(Path path) {
        cacheDir = path;
    }

    public void clear() {
        tasks.clear();
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    public ScannerTask create(LocalSong song, int songIndex, int songCount) {
        int taskNumber = tasks.size();
        ScannerTask task = new ScannerTask(this, taskNumber, song, songIndex, songCount, cacheDir);
        tasks.put(taskNumber, task);

        notifyClients(new Event(Type.ROWS_INSERTED, taskNumber));

        return task;
    }

    public ScannerTask getTask(int taskNumber) {
        return tasks.get(taskNumber);
    }

    public List<ScannerTask> getTasks() {
        return tasks.values().stream().toList();
    }

    public void notify_statusUpdated(int taskNumber) {
        notifyClients(new Event(Type.ROWS_UPDATED, taskNumber));
    }

    @Override
    public void addClient(Client<Event, TaskServer> client) {
        clientList.add(client);
        client.onAddClient(new TaskServer() {
            @Override
            public ResponseData getValue(int index) {
                ScannerTask task = tasks.get(index);
                if (task == null) {
                    return null;
                }

                ResponseData data = new ResponseData();
                data.composer = task.song.composer();
                data.count = task.songCount;
                data.dlc = task.song.dlc();
                data.index = task.songIndex;
                data.status = task.getStatus();
                data.tab = task.song.dlcTab();
                data.taskNumber = task.taskNumber;
                data.title = task.song.title();

                return data;
            }

            @Override
            public int getCount() {
                return tasks.size();
            }
        });
    }

    @Override
    public void notifyClients(Event data) {
        clientList.forEach((x) -> x.onNotify(data));
    }
}