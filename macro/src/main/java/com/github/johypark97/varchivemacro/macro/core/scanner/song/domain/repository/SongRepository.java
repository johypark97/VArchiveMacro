package com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.repository;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.List;
import java.util.Map;

public interface SongRepository {
    boolean isEmpty();

    void deleteAll();

    void saveAll(List<Song> songList);

    Song findSongById(int songId);

    List<Song> findAllSong();

    List<Pack> findAllPack();

    List<Category> findAllCategory();

    Map<Pack, List<Song>> groupSongByPack();

    Map<Category, List<Song>> groupSongByCategory();

    List<Song> filterSongByDuplicateTitle();
}
