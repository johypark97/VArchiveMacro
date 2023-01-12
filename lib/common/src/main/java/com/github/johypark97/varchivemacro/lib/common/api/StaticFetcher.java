package com.github.johypark97.varchivemacro.lib.common.api;

import com.github.johypark97.varchivemacro.lib.common.api.datastruct.staticfetcher.RemoteSong;
import java.io.IOException;
import java.util.List;

public interface StaticFetcher {
    List<RemoteSong> getSongs();

    void fetchSongs() throws IOException, InterruptedException;
}
