package com.github.johypark97.varchivemacro.macro.core.scanner.title.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.title.model.TitleJson;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultSongTitleRepository implements SongTitleRepository {
    private final Map<Integer, String> clippedTitleMap = new HashMap<>();
    private final Map<Integer, String> remoteTitleMap = new HashMap<>();
    private final Map<String, String> remappedTitleMap = new HashMap<>();

    public DefaultSongTitleRepository(Gson gson, Path titleDataPath) throws IOException {
        TitleJson titleJson;

        try (BufferedReader reader = Files.newBufferedReader(titleDataPath)) {
            titleJson = gson.fromJson(reader, TitleJson.class);
        }

        titleJson.clipped().forEach(x -> clippedTitleMap.put(x.id(), x.value()));
        titleJson.remap().forEach(x -> x.from().forEach(y -> remappedTitleMap.put(y, x.to())));
        titleJson.remoteTitle().forEach(x -> remoteTitleMap.put(x.id(), x.value()));
    }

    @Override
    public Optional<String> findClippedTitle(int songId) {
        return Optional.ofNullable(clippedTitleMap.get(songId));
    }

    @Override
    public Optional<String> findRemoteTitle(int songId) {
        return Optional.ofNullable(remoteTitleMap.get(songId));
    }

    @Override
    public Optional<String> findRemappedTitle(String scannedTitle) {
        return Optional.ofNullable(remappedTitleMap.get(scannedTitle));
    }
}
