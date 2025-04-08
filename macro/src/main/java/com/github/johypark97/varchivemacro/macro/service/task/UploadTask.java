package com.github.johypark97.varchivemacro.macro.service.task;

import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.data.Account;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.PathValidator;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData;
import com.github.johypark97.varchivemacro.macro.model.NewRecordData.Status;
import com.github.johypark97.varchivemacro.macro.repository.DatabaseRepository;
import com.github.johypark97.varchivemacro.macro.repository.RecordRepository;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UploadTask extends InterruptibleTask<Void> {
    private final String accountFile;
    private final int recordUploadDelay;

    private final WeakReference<DatabaseRepository> databaseRepositoryReference;
    private final WeakReference<NewRecordDataDomain> newRecordDataDomainReference;
    private final WeakReference<RecordRepository> recordRepositoryReference;

    public UploadTask(DatabaseRepository databaseRepository, RecordRepository recordRepository,
            NewRecordDataDomain newRecordDataDomain, String accountFile, int recordUploadDelay) {
        this.accountFile = accountFile;
        this.recordUploadDelay = recordUploadDelay;

        databaseRepositoryReference = new WeakReference<>(databaseRepository);
        newRecordDataDomainReference = new WeakReference<>(newRecordDataDomain);
        recordRepositoryReference = new WeakReference<>(recordRepository);
    }

    private DatabaseRepository getDatabaseRepository() {
        return databaseRepositoryReference.get();
    }

    private RecordRepository getRecordRepository() {
        return recordRepositoryReference.get();
    }

    private NewRecordDataDomain getNewRecordDataDomain() {
        return newRecordDataDomainReference.get();
    }

    private RecordUploader createRecordUploader() throws IOException, GeneralSecurityException {
        Account account = new Account(PathValidator.validateAndConvert(accountFile));
        return Api.newRecordUploader(account.userNo, account.token);
    }

    private Queue<NewRecordData> createUploadQueue() {
        EnumSet<Status> statusSet = EnumSet.of(Status.HIGHER_RECORD_EXISTS, Status.UPLOADED);

        return getNewRecordDataDomain().copyNewRecordDataList().stream()
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
        if (getNewRecordDataDomain().isEmpty()) {
            throw new IllegalStateException("NewRecordDataDomain is empty");
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

                getRecordRepository().updateRecord(newRecord);
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
            getRecordRepository().save();
        }

        return null;
    }
}
