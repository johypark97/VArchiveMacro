package com.github.johypark97.varchivemacro.macro.core.scanner;

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
import com.github.johypark97.varchivemacro.macro.core.scanner.ResultManager.RecordData.Result;
import com.github.johypark97.varchivemacro.macro.core.scanner.ScannerTask.AnalyzedData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.Event.Type;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResponseData;
import com.github.johypark97.varchivemacro.macro.gui.model.ScannerResultListModels.ResultListProvider;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ResultManager implements Server<Event, ResultListProvider> {
    private final List<Client<Event, ResultListProvider>> clientList = new CopyOnWriteArrayList<>();
    private final List<RecordData> records = new CopyOnWriteArrayList<>();

    private SongRecordManager songRecordManager;

    public void setModels(SongRecordManager songRecordManager) {
        this.songRecordManager = songRecordManager;
    }

    public void clearRecords() {
        records.clear();
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    public void addRecords(List<ScannerTask> tasks) {
        List<RecordData> dataList = new ArrayList<>();

        tasks.forEach((task) -> {
            LocalSong song = task.song;

            task.getAnalyzedDataCellSet().forEach((cell) -> {
                AnalyzedData analyzedData = cell.getValue();

                // The rate will be negative when an OCRed rateText is invalid.
                if (analyzedData.rate == -1) {
                    return;
                }

                Api.Button button = cell.getRowKey().toApi();
                Api.Pattern pattern = cell.getColumnKey().toApi();
                float rate = analyzedData.rate;
                boolean maxCombo = analyzedData.isMaxCombo;

                LocalRecord newRecord = new LocalRecord(song.id(), button, pattern, rate, maxCombo);
                LocalRecord oldRecord = songRecordManager.findSameRecord(newRecord);

                if (oldRecord != null && oldRecord.isUpdated(newRecord)) {
                    RecordData data = new RecordData(dataList.size());
                    data.newRecord = newRecord;
                    data.oldMaxCombo = oldRecord.maxCombo;
                    data.oldRate = oldRecord.rate;
                    data.song = song;
                    data.taskNumber = task.taskNumber;

                    dataList.add(data);
                }
            });
        });

        records.addAll(dataList);
        notifyClients(new Event(Type.DATA_CHANGED));
    }

    public void upload(Path accountPath, int delay) throws IOException, GeneralSecurityException {
        Account account = new Account(accountPath);
        RecordUploader api = Api.newRecordUploader(account.userNo, account.token);

        EnumSet<Result> filter = EnumSet.of(Result.CANCELED, Result.NOT_UPLOADED, Result.SUSPENDED);
        Queue<RecordData> queue =
                records.stream().filter((x) -> x.isSelected && filter.contains(x.result))
                        .collect(Collectors.toCollection(LinkedList::new));

        try {
            while (queue.peek() != null) {
                RecordData data = queue.poll();

                data.result = Result.UPLOADING;
                notifyClients(new Event(Type.ROWS_UPDATED, data.recordNumber));

                RequestJson requestJson = recordToRequest(data.song, data.newRecord);
                api.upload(requestJson); // Throw an RuntimeException when an error occurs.

                data.result = api.getResult() ? Result.UPLOADED : Result.HIGHER_RECORD_EXISTS;
                songRecordManager.updateRecord(data.newRecord);
                notifyClients(new Event(Type.ROWS_UPDATED, data.recordNumber));

                TimeUnit.MILLISECONDS.sleep(delay);
            }
        } catch (InterruptedException e) {
            while (queue.peek() != null) {
                RecordData data = queue.poll();
                data.result = Result.CANCELED;
            }
        } finally {
            while (queue.peek() != null) {
                RecordData data = queue.poll();
                data.result = Result.SUSPENDED;
            }
        }

        songRecordManager.saveRecord();
        notifyClients(new Event(Type.DATA_CHANGED));
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
    public void addClient(Client<Event, ResultListProvider> client) {
        clientList.add(client);
        client.onAddClient(new ResultListProvider() {
            @Override
            public ResponseData getValue(int index) {
                RecordData recordData = records.get(index);

                ResponseData data = new ResponseData();
                data.button = Button.valueOf(recordData.newRecord.button);
                data.composer = recordData.song.composer();
                data.dlc = recordData.song.dlc();
                data.isSelected = recordData.isSelected;
                data.newMaxCombo = recordData.newRecord.maxCombo;
                data.newRate = recordData.newRecord.rate;
                data.oldMaxCombo = recordData.oldMaxCombo;
                data.oldRate = recordData.oldRate;
                data.pattern = Pattern.valueOf(recordData.newRecord.pattern);
                data.resultNumber = recordData.recordNumber;
                data.status = recordData.result;
                data.taskNumber = recordData.taskNumber;
                data.title = recordData.song.title();

                return data;
            }

            @Override
            public int getCount() {
                return records.size();
            }

            @Override
            public void updateSelected(int index, boolean value) {
                RecordData recordData = records.get(index);
                if (recordData != null) {
                    recordData.isSelected = value;
                    ResultManager.this.notifyClients(new Event(Type.ROWS_UPDATED, index));
                }
            }
        });
    }

    @Override
    public void notifyClients(Event data) {
        clientList.forEach((x) -> x.onNotify(data));
    }


    public static class RecordData {
        public final int recordNumber;
        public LocalRecord newRecord;
        public LocalSong song;
        public Result result = Result.NOT_UPLOADED;
        public boolean isSelected = true;
        public boolean oldMaxCombo;
        public float oldRate;
        public int taskNumber;

        public RecordData(int recordNumber) {
            this.recordNumber = recordNumber;
        }

        public enum Result {
            CANCELED, HIGHER_RECORD_EXISTS, NOT_UPLOADED, SUSPENDED, UPLOADED, UPLOADING, WAITING
        }
    }
}
