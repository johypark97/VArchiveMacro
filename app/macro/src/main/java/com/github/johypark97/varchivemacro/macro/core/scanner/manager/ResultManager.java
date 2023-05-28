package com.github.johypark97.varchivemacro.macro.core.scanner.manager;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;

public interface ResultManager {
    void clear();

    void addAll(TaskManager taskManager);

    void upload(Path accountPath, int delay) throws IOException, GeneralSecurityException;

    enum ResultStatus {
        CANCELED, HIGHER_RECORD_EXISTS, NOT_UPLOADED, SUSPENDED, UPLOADED, UPLOADING
    }
}
