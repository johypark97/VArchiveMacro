package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.service;

import com.github.johypark97.varchivemacro.lib.scanner.api.Api;
import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordButtonConverter;
import com.github.johypark97.varchivemacro.macro.common.converter.RecordPatternConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class RecordUploadService {
    private final RecordUploader uploader;

    public RecordUploadService(Account account) throws GeneralSecurityException {
        uploader = Api.newRecordUploader(account.userNo(), account.token());
    }

    public boolean upload(String title, String composer, RecordButton button, RecordPattern pattern,
            SongRecord record) throws IOException, InterruptedException, ApiException {
        RecordUploader.RequestJson json =
                new RecordUploader.RequestJson(title, RecordButtonConverter.toLib(button),
                        RecordPatternConverter.toLib(pattern), record.rate(), record.maxCombo());

        if (composer != null) {
            json.composer = composer;
        }

        uploader.upload(json);

        return uploader.getResult();
    }
}
