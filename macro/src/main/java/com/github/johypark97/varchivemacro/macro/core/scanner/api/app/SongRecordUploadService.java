package com.github.johypark97.varchivemacro.macro.core.scanner.api.app;

import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.service.AccountFileLoadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.service.RecordUploadService;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public class SongRecordUploadService {
    private final RecordUploadService recordUploadService;

    public SongRecordUploadService(Path accountFilePath)
            throws IOException, InvalidAccountFileException, GeneralSecurityException {
        Account account = new AccountFileLoadService(accountFilePath).load();

        recordUploadService = new RecordUploadService(account);
    }

    public boolean upload(String title, String composer, RecordButton button, RecordPattern pattern,
            SongRecord record) throws IOException, InterruptedException, ApiException {
        return recordUploadService.upload(title, composer, button, pattern, record);
    }
}
