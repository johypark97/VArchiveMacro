package com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.List;
import java.util.Map;

public interface SongRepository {
    void load();

    Song findSongById(int songId);

    List<Song> findAllSong();

    List<Song.Pack> findAllPack();

    List<Song.Pack.Category> findAllCategory();

    Map<Song.Pack, List<Song>> groupSongByPack();

    Map<Song.Pack.Category, List<Song>> groupSongByCategory();

    List<Song> filterSongByDuplicateTitle();
}
