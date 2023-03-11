package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.util.LocalSongComparator;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SongManager {
    protected final List<LocalSong> songs;

    protected final Map<Integer, LocalSong> lookupId;

    public SongManager(Path songPath) throws IOException {
        songs = LocalSong.loadJson(songPath);

        lookupId = newLookupId(songs);
    }

    public int songCount() {
        return songs.size();
    }

    public LocalSong getSong(int id) {
        return lookupId.get(id);
    }

    public List<LocalSong> getSongs() {
        return songs.stream().sorted(new LocalSongComparator()).toList();
    }

    public Set<Integer> duplicateTitleSet() {
        Map<String, List<LocalSong>> map = new HashMap<>();
        songs.forEach((song) -> {
            List<LocalSong> list = map.computeIfAbsent(song.title(), (x) -> new ArrayList<>());
            list.add(song);
        });

        return map.values().stream().filter((x) -> x.size() > 1).flatMap(Collection::stream)
                .map(LocalSong::id).collect(Collectors.toSet());
    }

    private Map<Integer, LocalSong> newLookupId(List<LocalSong> songs) {
        Function<LocalSong, Integer> keyMapper = LocalSong::id;
        Function<LocalSong, LocalSong> valueMapper = (x) -> x;

        return songs.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
