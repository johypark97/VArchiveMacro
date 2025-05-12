package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.infrastructure.api.uploader.SongRecordUploader;

public interface SongRecordUploaderFactory {
    SongRecordUploader create(String accountFile) throws Exception;
}
