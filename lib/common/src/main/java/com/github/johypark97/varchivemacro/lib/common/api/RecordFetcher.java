package com.github.johypark97.varchivemacro.lib.common.api;

import com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher.Success;
import java.io.IOException;

public interface RecordFetcher {
    Success getResult();

    void fetch(Button button, Board board) throws IOException, InterruptedException;
}
