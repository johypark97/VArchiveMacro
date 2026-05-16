package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Song;
import java.util.List;
import java.util.Map;

public interface SongDatabase {
    Category getCategory(String name);

    Pack getPack(String name);

    Song getSong(int id);

    List<Category> categoryList();

    List<Pack> packList();

    List<Song> songList();

    Map<Pack, List<Song>> packSongListMap();

    Map<Category, List<Song>> categorySongListMap();
}
