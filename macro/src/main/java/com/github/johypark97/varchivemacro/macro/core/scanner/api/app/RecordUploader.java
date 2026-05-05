package com.github.johypark97.varchivemacro.macro.core.scanner.api.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.ApiException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.uploader.RequestJson;
import java.io.IOException;

public interface RecordUploader {
    boolean getResult();

    void upload(RequestJson data) throws IOException, InterruptedException, ApiException;
}
