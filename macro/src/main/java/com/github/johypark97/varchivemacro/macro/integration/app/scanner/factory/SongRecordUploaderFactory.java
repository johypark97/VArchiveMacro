package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.uploader.SongRecordUploader;

public interface SongRecordUploaderFactory {
    SongRecordUploader create(String accountFile) throws Exception;
}
