package com.github.johypark97.varchivemacro.macro.core.scanner.api.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.RemoteSong;
import java.io.IOException;
import java.util.List;

public interface StaticFetcher {
    List<RemoteSong> getSongs();

    void fetchSongs() throws IOException, InterruptedException;
}
