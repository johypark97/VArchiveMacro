package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.LocalSongComparator;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
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

public class SongManager implements ISongManager {
    private List<LocalSong> songList;

    private Map<Integer, LocalSong> lookupId;

    public void load(Path songPath) throws IOException {
        songList = LocalSong.loadJson(songPath);

        lookupId = newLookupId(songList);
    }

    private Map<Integer, LocalSong> newLookupId(List<LocalSong> songs) {
        Function<LocalSong, Integer> keyMapper = LocalSong::id;
        Function<LocalSong, LocalSong> valueMapper = (x) -> x;

        return songs.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public LocalSong getSong(int id) {
        return lookupId.get(id);
    }

    @Override
    public List<LocalSong> getSongList() {
        return songList.stream().sorted(new LocalSongComparator()).toList();
    }

    @Override
    public Set<Integer> getDuplicateTitleSet() {
        Map<String, List<LocalSong>> map = new HashMap<>();
        songList.forEach((song) -> {
            List<LocalSong> list = map.computeIfAbsent(song.title(), (x) -> new ArrayList<>());
            list.add(song);
        });

        return map.values().stream().filter((x) -> x.size() > 1).flatMap(Collection::stream)
                .map(LocalSong::id).collect(Collectors.toSet());
    }
}
