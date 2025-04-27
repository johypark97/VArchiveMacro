package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.repository;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import com.github.johypark97.varchivemacro.macro.domain.scanner.repository.SongRepository;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.converter.SongDatabaseConverter;
import com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader.SongRepositoryLoader;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSongRepository implements SongRepository {
    private final SongRepositoryLoader loader;

    private SongDatabase songDatabase;

    public DefaultSongRepository(SongRepositoryLoader loader) {
        this.loader = loader;
    }

    @Override
    public void load() {
        try {
            songDatabase = loader.load();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Song findSongById(int songId) {
        return SongDatabaseConverter.toDomainSong(songDatabase.getSong(songId));
    }

    @Override
    public List<Song> findAllSong() {
        return songDatabase.songList().stream().map(SongDatabaseConverter::toDomainSong).toList();
    }

    @Override
    public List<Song.Pack> findAllPack() {
        return songDatabase.packList().stream().map(SongDatabaseConverter::toDomainPack).toList();
    }

    @Override
    public List<Song.Pack.Category> findAllCategory() {
        return songDatabase.categoryList().stream().map(SongDatabaseConverter::toDomainCategory)
                .toList();
    }

    @Override
    public Map<Song.Pack, List<Song>> groupSongByPack() {
        return songDatabase.packSongListMap().entrySet().stream().collect(
                Collectors.toMap(entry -> SongDatabaseConverter.toDomainPack(entry.getKey()),
                        entry -> entry.getValue().stream().map(SongDatabaseConverter::toDomainSong)
                                .toList(),
                        (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList(),
                        LinkedHashMap::new));
    }

    @Override
    public Map<Song.Pack.Category, List<Song>> groupSongByCategory() {
        return songDatabase.categorySongListMap().entrySet().stream().collect(
                Collectors.toMap(entry -> SongDatabaseConverter.toDomainCategory(entry.getKey()),
                        entry -> entry.getValue().stream().map(SongDatabaseConverter::toDomainSong)
                                .toList(),
                        (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).toList(),
                        LinkedHashMap::new));
    }

    @Override
    public List<Song> filterSongByDuplicateTitle() {
        return songDatabase.songList().stream()
                .collect(Collectors.groupingBy(SongDatabase.Song::title)).entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .flatMap(entry -> entry.getValue().stream())
                .map(SongDatabaseConverter::toDomainSong).toList();
    }
}
