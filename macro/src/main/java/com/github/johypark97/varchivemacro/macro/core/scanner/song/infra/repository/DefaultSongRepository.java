package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.comparator.SongPackComparator;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.comparator.SongTitleComparator;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository.SongRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultSongRepository implements SongRepository {
    // name - category
    private final Map<String, Category> categoryMap = new HashMap<>();

    // name - pack
    private final Map<String, Pack> packMap = new HashMap<>();

    // id - song
    private final Map<Integer, Song> songMap = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return songMap.isEmpty();
    }

    @Override
    public void deleteAll() {
        categoryMap.clear();
        packMap.clear();
        songMap.clear();
    }

    @Override
    public void saveAll(List<Song> songList) {
        songList.forEach(song -> {
            categoryMap.putIfAbsent(song.pack().category().name(), song.pack().category());
            packMap.putIfAbsent(song.pack().name(), song.pack());
            songMap.putIfAbsent(song.songId(), song);
        });
    }

    @Override
    public Song findSongById(int songId) {
        return songMap.get(songId);
    }

    @Override
    public List<Song> findAllSong() {
        return songMap.values().stream().sorted(new SongTitleComparator()).toList();
    }

    @Override
    public List<Pack> findAllPack() {
        return packMap.values().stream().sorted(Pack::compareTo).toList();
    }

    @Override
    public List<Category> findAllCategory() {
        return categoryMap.values().stream().sorted(Category::compareTo).toList();
    }

    @Override
    public Map<Pack, List<Song>> groupSongByPack() {
        Map<Pack, List<Song>> map = new LinkedHashMap<>();

        packMap.values()
                .stream()
                .sorted(Pack::compareTo)
                .forEach(x -> map.put(x, new ArrayList<>()));

        songMap.values()
                .stream()
                .sorted(new SongPackComparator())
                .forEach(x -> map.get(x.pack()).add(x));

        return map;
    }

    @Override
    public Map<Category, List<Song>> groupSongByCategory() {
        Map<Category, List<Song>> map = new LinkedHashMap<>();

        categoryMap.values()
                .stream()
                .sorted(Category::compareTo)
                .forEach(x -> map.put(x, new ArrayList<>()));

        songMap.values()
                .stream()
                .sorted(new SongPackComparator())
                .forEach(x -> map.get(x.pack().category()).add(x));

        return map;
    }

    @Override
    public List<Song> filterSongByDuplicateTitle() {
        return songMap.values()
                .stream()
                .collect(Collectors.groupingBy(Song::title))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }
}
