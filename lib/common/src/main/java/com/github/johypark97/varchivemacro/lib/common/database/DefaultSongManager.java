package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.database.comparator.LocalSongComparator;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.SongData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultSongManager implements SongManager {
    private static final Predicate<Collection<?>> moreThanOneElement = (x) -> x.size() > 1;

    private final List<LocalSong> songList;

    private final Map<Integer, LocalSong> lookupId;

    public DefaultSongManager(Path songPath) throws IOException {
        List<SongData> songDataList = SongData.loadJson(songPath);

        songList = songDataList.stream().map(SongData::toLocalSong).toList();
        lookupId = newLookupId(songList);
    }

    private static Map<Integer, LocalSong> newLookupId(List<LocalSong> songList) {
        Function<LocalSong, Integer> keyMapper = (x) -> x.id;
        Function<LocalSong, LocalSong> valueMapper = (x) -> x;

        return songList.stream().collect(Collectors.toMap(keyMapper, valueMapper));
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
            List<LocalSong> list = map.computeIfAbsent(song.title, (x) -> new ArrayList<>());
            list.add(song);
        });

        return map.values().stream().filter(moreThanOneElement).flatMap(Collection::stream)
                .map((x) -> x.id).collect(Collectors.toSet());
    }
}
