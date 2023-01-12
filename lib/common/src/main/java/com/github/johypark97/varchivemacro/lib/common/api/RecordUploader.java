package com.github.johypark97.varchivemacro.lib.common.api;

import com.github.johypark97.varchivemacro.lib.common.api.datastruct.recorduploader.RequestData;
import java.io.IOException;

public interface RecordUploader {
    boolean getResult();

    void upload(RequestData data) throws IOException, InterruptedException;
}
