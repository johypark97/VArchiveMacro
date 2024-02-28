package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.lib.common.api.ApiException;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestJson;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.RecordManager;
import com.github.johypark97.varchivemacro.lib.common.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.macro.core.SongRecordManager;
import com.github.johypark97.varchivemacro.macro.core.exception.RecordNotLoadedException;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.ResultManager;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.ResultManager.ResultData;
import com.github.johypark97.varchivemacro.macro.core.scanner.manager.ResultManager.ResultStatus;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Path accountPath;
    private final SongRecordManager songRecordManager;
    private final int delay;

    public Exception exception;

    public UploadService(SongRecordManager songRecordManager, Path accountPath, int delay) {
        this.accountPath = accountPath;
        this.delay = delay;
        this.songRecordManager = songRecordManager;
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void await() throws InterruptedException {
        if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
            throw new RuntimeException("unexpected timeout");
        }
    }

    public void execute(ResultManager resultManager) {
        executor.execute(() -> {
            try {
                RecordManager recordManager = songRecordManager.getRecordManager();
                if (recordManager == null) {
                    throw new RecordNotLoadedException();
                }

                Account account = new Account(accountPath);
                RecordUploader api = Api.newRecordUploader(account.userNo, account.token);

                Queue<ResultData> queue = new LinkedList<>();

                EnumSet<ResultStatus> statusFilter =
                        EnumSet.of(ResultStatus.CANCELED, ResultStatus.NOT_UPLOADED,
                                ResultStatus.SUSPENDED);
                for (ResultData data : resultManager) {
                    if (data.isSelected() && statusFilter.contains(data.getStatus())) {
                        queue.add(data);
                    }
                }

                try {
                    while (queue.peek() != null) {
                        ResultData data = queue.poll();

                        data.setStatus(ResultStatus.UPLOADING);

                        RequestJson requestJson =
                                recordToRequest(data.getSong(), data.getNewRecord());
                        api.upload(requestJson);

                        recordManager.updateRecord(data.getNewRecord());
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
            } catch (ApiException | GeneralSecurityException | IOException |
                    RecordNotLoadedException e) {
                LOGGER.atError().log(e.getMessage(), e);
                exception = e;
            }
        });
        executor.shutdown();
    }

    private RequestJson recordToRequest(LocalDlcSong song, LocalRecord record) {
        String title = song.remoteTitle;
        if (title == null) {
            title = song.title;
        }

        RequestJson requestJson =
                new RequestJson(title, record.button, record.pattern, record.rate, record.maxCombo);
        if (songRecordManager.getDuplicateTitleSet().contains(song.id)) {
            requestJson.composer = song.composer;
        }

        return requestJson;
    }
}
