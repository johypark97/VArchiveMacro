package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.data.Account;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData.Status;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UploadTask extends InterruptibleTask<Void> {
    private final Path accountPath;
    private final int recordUploadDelay;

    private final WeakReference<DatabaseRepository> databaseRepositoryReference;
    private final WeakReference<NewRecordDataManager> newRecordDataManagerReference;
    private final WeakReference<RecordModel> recordModelWeakReference;

    public UploadTask(DatabaseRepository databaseRepository, RecordModel recordModel,
            NewRecordDataManager newRecordDataManager, Path accountPath, int recordUploadDelay) {
        this.accountPath = accountPath;
        this.recordUploadDelay = recordUploadDelay;

        databaseRepositoryReference = new WeakReference<>(databaseRepository);
        newRecordDataManagerReference = new WeakReference<>(newRecordDataManager);
        recordModelWeakReference = new WeakReference<>(recordModel);
    }

    private DatabaseRepository getDatabaseRepository() {
        return databaseRepositoryReference.get();
    }

    private RecordModel getRecordModel() {
        return recordModelWeakReference.get();
    }

    private NewRecordDataManager getNewRecordDataManager() {
        return newRecordDataManagerReference.get();
    }

    private RecordUploader createRecordUploader() throws IOException, GeneralSecurityException {
        Account account = new Account(accountPath);
        return Api.newRecordUploader(account.userNo, account.token);
    }

    private Queue<NewRecordData> createUploadQueue() {
        EnumSet<Status> statusSet = EnumSet.of(Status.HIGHER_RECORD_EXISTS, Status.UPLOADED);

        return getNewRecordDataManager().copyNewRecordDataList().stream()
                .filter(x -> x.selected.get() && !statusSet.contains(x.status.get()))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private RequestJson recordToRequest(Song song, LocalRecord record) {
        String title = getDatabaseRepository().getRemoteTitle(song.id());
        if (title == null) {
            title = song.title();
        }

        RequestJson requestJson =
                new RequestJson(title, record.button, record.pattern, record.rate, record.maxCombo);
        if (getDatabaseRepository().duplicateTitleSongIdSet().contains(song.id())) {
            requestJson.composer = song.composer();
        }

        return requestJson;
    }

    @Override
    protected Void callTask() throws Exception {
        // throw an exception if there is no new record data
        if (getNewRecordDataManager().isEmpty()) {
            throw new IllegalStateException("NewRecordDataManager is empty");
        }

        RecordUploader api = createRecordUploader();

        Queue<NewRecordData> queue = createUploadQueue();

        boolean isFirst = true;
        try {
            while (true) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    TimeUnit.MILLISECONDS.sleep(recordUploadDelay);
                }

                NewRecordData data = queue.poll();
                if (data == null) {
                    break;
                }

                data.status.set(Status.UPLOADING);

                Song song = data.songProperty().get();
                LocalRecord newRecord = data.newRecordProperty().get();

                RequestJson requestJson = recordToRequest(song, newRecord);
                api.upload(requestJson);

                getRecordModel().updateRecord(newRecord);
                data.status.set(api.getResult() ? Status.UPLOADED : Status.HIGHER_RECORD_EXISTS);
            }
        } catch (InterruptedException e) {
            while (true) {
                NewRecordData data = queue.poll();
                if (data == null) {
                    break;
                }

                data.status.set(Status.CANCELED);
            }
        } finally {
            getRecordModel().save();
        }

        return null;
    }
}
