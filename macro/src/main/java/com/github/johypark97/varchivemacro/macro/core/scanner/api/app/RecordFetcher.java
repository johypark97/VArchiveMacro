package com.github.johypark97.varchivemacro.macro.core.scanner.api.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.ApiException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.fetcher.SuccessJson;
import com.github.johypark97.varchivemacro.macro.libscanner.Enums.Button;
import java.io.IOException;

public interface RecordFetcher {
    SuccessJson getResult();

    void fetch(Button button) throws IOException, InterruptedException, ApiException;
}
