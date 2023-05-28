package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalRecord;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Client;
import com.github.johypark97.varchivemacro.macro.core.protocol.SyncChannel.Server;
import com.github.johypark97.varchivemacro.macro.core.scanner.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.TaskManager.TaskData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event.Type;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResultListProvider;
import com.google.common.collect.Table.Cell;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DefaultResultManager implements ResultManager, Server<Event, ResultListProvider> {
    private final List<Client<Event, ResultListProvider>> clientList = new CopyOnWriteArrayList<>();
    private final List<ResultData> resultDataList = new CopyOnWriteArrayList<>();

    private SongRecordManager songRecordManager;

    public void setModels(SongRecordManager songRecordManager) {
        this.songRecordManager = songRecordManager;
    }

    private float parseRateText(String text) {
        int index = text.indexOf('%');
        if (index == -1) {
            return -1;
        }

        try {
            String s = text.substring(0, index);
            float value = Float.parseFloat(s);
            return (value >= 0 && value <= 100) ? value : -1;
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return -1;
        }
    }

    private RequestJson recordToRequest(LocalSong song, LocalRecord record) {
        String title = song.remote_title();
        if (title == null) {
            title = song.title();
        }

        RequestJson requestJson =
                new RequestJson(title, record.button, record.pattern, record.rate, record.maxCombo);
        if (songRecordManager.getDuplicateTitleSet().contains(song.id())) {
            requestJson.composer = song.composer();
        }

        return requestJson;
    }

    @Override
    public void clear() {
        resultDataList.clear();
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    @Override
    public void addAll(TaskManager taskManager) {
        for (TaskData taskData : taskManager) {
            LocalSong song = taskData.getSong();

            for (Cell<Button, Pattern, AnalyzedData> cell : taskData) {
                AnalyzedData analyzedData = cell.getValue();

                float rate = parseRateText(analyzedData.getRateText());
                if (rate == -1) {
                    continue;
                }

                Api.Button button = cell.getRowKey().toApi();
                Api.Pattern pattern = cell.getColumnKey().toApi();
                boolean maxCombo = analyzedData.isMaxCombo();

                LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, rate, maxCombo);
                LocalRecord oldRecord = songRecordManager.findSameRecord(newRecord);

                if (oldRecord != null && oldRecord.isUpdated(newRecord)) {
                    int resultNumber = resultDataList.size();

                    ResultData data = new ResultData(resultNumber, taskData, oldRecord, newRecord);
                    resultDataList.add(data);
                }
            }
        }

        notifyClients(new Event(Type.DATA_CHANGED));
    }

    @Override
    public void upload(Path accountPath, int delay) throws IOException, GeneralSecurityException {
        Account account = new Account(accountPath);
        RecordUploader api = Api.newRecordUploader(account.userNo, account.token);

        EnumSet<ResultStatus> statusFilter =
                EnumSet.of(ResultStatus.CANCELED, ResultStatus.NOT_UPLOADED,
                        ResultStatus.SUSPENDED);
        Queue<ResultData> queue = resultDataList.stream()
                .filter((x) -> x.isSelected() && statusFilter.contains(x.getStatus()))
                .collect(Collectors.toCollection(LinkedList::new));

        try {
            while (queue.peek() != null) {
                ResultData data = queue.poll();

                data.setStatus(ResultStatus.UPLOADING);

                RequestJson requestJson = recordToRequest(data.song, data.newRecord);
                api.upload(requestJson); // Throw an RuntimeException when an error occurs.

                songRecordManager.updateRecord(data.newRecord);
                data.setStatus(api.getResult()
                        ? ResultStatus.UPLOADED
                        : ResultStatus.HIGHER_RECORD_EXISTS);

                TimeUnit.MILLISECONDS.sleep(delay);
            }
        } catch (InterruptedException e) {
            while (queue.peek() != null) {
                ResultData data = queue.poll();
                data.setStatus(ResultStatus.CANCELED);
            }
        } finally {
            while (queue.peek() != null) {
                ResultData data = queue.poll();
                data.setStatus(ResultStatus.SUSPENDED);
            }
        }

        songRecordManager.saveRecord();
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    @Override
    public void addClient(Client<Event, ResultListProvider> client) {
        clientList.add(client);
        client.onAddClient(new ResultListProvider() {
            @Override
            public ResponseData getValue(int index) {
                ResultData resultData = resultDataList.get(index);

                ResponseData data = new ResponseData();
                data.button = Button.valueOf(resultData.newRecord.button);
                data.composer = resultData.song.composer();
                data.dlc = resultData.song.dlc();
                data.isSelected = resultData.isSelected();
                data.newMaxCombo = resultData.newRecord.maxCombo;
                data.newRate = resultData.newRecord.rate;
                data.oldMaxCombo = resultData.oldMaxCombo;
                data.oldRate = resultData.oldRate;
                data.pattern = Pattern.valueOf(resultData.newRecord.pattern);
                data.resultNumber = resultData.resultNumber;
                data.status = resultData.getStatus();
                data.taskNumber = resultData.taskNumber;
                data.title = resultData.song.title();

                return data;
            }

            @Override
            public int getCount() {
                return resultDataList.size();
            }

            @Override
            public void updateSelected(int index, boolean value) {
                ResultData data = resultDataList.get(index);
                if (data != null) {
                    data.updateSelected(value);
                }
            }
        });
    }

    @Override
    public void notifyClients(Event data) {
        clientList.forEach((x) -> x.onNotify(data));
    }

    private class ResultData {
        public final LocalRecord newRecord;
        public final LocalSong song;
        public final boolean oldMaxCombo;
        public final float oldRate;
        public final int resultNumber;
        public final int taskNumber;

        private ResultStatus status = ResultStatus.NOT_UPLOADED;
        private boolean selected = true;

        public ResultData(int resultNumber, TaskData taskData, LocalRecord oldRecord,
                LocalRecord newRecord) {
            this.newRecord = newRecord;
            this.resultNumber = resultNumber;

            oldMaxCombo = oldRecord.maxCombo;
            oldRate = oldRecord.rate;
            song = taskData.getSong();
            taskNumber = taskData.getTaskNumber();
        }

        public ResultStatus getStatus() {
            return status;
        }

        public void setStatus(ResultStatus value) {
            status = value;
            notifyClients(new Event(Type.ROWS_UPDATED, resultNumber));
        }

        public boolean isSelected() {
            return selected;
        }

        public void updateSelected(boolean value) {
            selected = value;
            notifyClients(new Event(Type.ROWS_UPDATED, resultNumber));
        }
    }
}
