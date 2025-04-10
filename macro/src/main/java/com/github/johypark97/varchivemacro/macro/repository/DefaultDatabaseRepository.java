package com.github.johypark97.varchivemacro.macro.repository;

import com.github.johypark97.varchivemacro.lib.scanner.database.CachedReadOnlySongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Category;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDatabaseRepository implements DatabaseRepository {
    private static final Path BASE_PATH = Path.of("data");

    private static final Path SONG_DATABASE_PATH = BASE_PATH.resolve("song.db");
    private static final Path TITLE_PATH = BASE_PATH.resolve("titles.json");

    private SongDatabase songDatabase;
    private TitleTool titleTool;

    @Override
    public void load() throws IOException, SQLException {
        songDatabase = new CachedReadOnlySongDatabase(SONG_DATABASE_PATH);
        titleTool = new DefaultTitleTool(TITLE_PATH);
    }

    @Override
    public TitleTool getTitleTool() {
        return titleTool;
    }

    @Override
    public Song getSong(int id) {
        return songDatabase.getSong(id);
    }

    @Override
    public List<String> categoryNameList() {
        return songDatabase.categoryList().stream().map(Category::name).toList();
    }

    @Override
    public Map<String, List<Song>> categoryNameSongListMap() {
        return songDatabase.categorySongListMap().entrySet().stream().collect(
                Collectors.toMap(x -> x.getKey().name(), Map.Entry::getValue, (o, o2) -> null,
                        LinkedHashMap::new));
    }

    @Override
    public Set<Integer> duplicateTitleSongIdSet() {
        return songDatabase.songList().stream().filter(x -> x.priority() > 0).map(Song::id)
                .collect(Collectors.toSet());
    }

    @Override
    public String getRemoteTitleOrDefault(Song song) {
        return titleTool.getRemoteTitleOrDefault(song);
    }
}
