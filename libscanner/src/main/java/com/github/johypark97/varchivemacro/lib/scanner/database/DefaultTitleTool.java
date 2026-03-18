package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.TitleData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DefaultTitleTool implements TitleTool {
    private final Map<Integer, String> clippedTitleMap = new HashMap<>();
    private final Map<Integer, String> remoteTitleMap = new HashMap<>();
    private final Map<String, String> scannedTitleRemap = new HashMap<>();

    public DefaultTitleTool(Path path) throws IOException {
        TitleData titleData = TitleData.loadJson(path);

        titleData.clipped.forEach(x -> clippedTitleMap.put(x.id(), x.value()));
        titleData.remap.forEach(x -> x.from.forEach(y -> scannedTitleRemap.put(y, x.to)));
        titleData.remoteTitle.forEach(x -> remoteTitleMap.put(x.id(), x.value()));
    }

    @Override
    public boolean hasClippedTitle(Song song) {
        return clippedTitleMap.containsKey(song.id());
    }

    @Override
    public String getClippedTitleOrDefault(Song song) {
        return clippedTitleMap.getOrDefault(song.id(), song.title());
    }

    @Override
    public String getRemoteTitleOrDefault(Song song) {
        return remoteTitleMap.getOrDefault(song.id(), song.title());
    }

    @Override
    public String remapScannedTitle(String value) {
        return scannedTitleRemap.getOrDefault(value, value);
    }
}
