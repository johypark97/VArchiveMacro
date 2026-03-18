package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.SongPackComparator;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.SongTitleComparator;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.sqlite.SQLiteConfig;

public class CachedReadOnlySongDatabase implements SongDatabase {
    private final Map<String, Category> categoryMap = new LinkedHashMap<>(); // name - category
    private final Map<String, Pack> packMap = new LinkedHashMap<>(); // name - pack
    private final Map<Integer, Song> songMap = new LinkedHashMap<>(); // id - song

    public CachedReadOnlySongDatabase(Path path) throws SQLException {
        String url = "jdbc:sqlite:" + path;

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);

        try (Connection connection = config.createConnection(url);
                Statement statement = connection.createStatement()) {
            // query category
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Category")) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int priority = resultSet.getInt("priority");

                    Category category = new Category(name, priority);

                    categoryMap.put(name, category);
                }
            }

            // query pack
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Pack")) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int priority = resultSet.getInt("priority");
                    String category = resultSet.getString("category");

                    Pack pack = new Pack(name, priority, categoryMap.get(category));

                    packMap.put(name, pack);
                }
            }

            // query song
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM SongView")) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String composer = resultSet.getString("composer");
                    int priority = resultSet.getInt("priority");
                    String pack = resultSet.getString("pack");

                    Song song = new Song(id, title, composer, priority, packMap.get(pack));

                    songMap.put(id, song);
                }
            }
        }
    }

    @Override
    public Category getCategory(String name) {
        return categoryMap.get(name);
    }

    @Override
    public Pack getPack(String name) {
        return packMap.get(name);
    }

    @Override
    public Song getSong(int id) {
        return songMap.get(id);
    }

    @Override
    public List<Category> categoryList() {
        return categoryMap.values().stream().sorted(Category::compareTo).toList();
    }

    @Override
    public List<Pack> packList() {
        return packMap.values().stream().sorted(Pack::compareTo).toList();
    }

    @Override
    public List<Song> songList() {
        return songMap.values().stream().sorted(new SongTitleComparator()).toList();
    }

    @Override
    public Map<Pack, List<Song>> packSongListMap() {
        Map<Pack, List<Song>> map = new LinkedHashMap<>();

        packMap.values().stream().sorted(Pack::compareTo)
                .forEach(x -> map.put(x, new ArrayList<>()));

        songMap.values().stream().sorted(new SongPackComparator())
                .forEach(x -> map.get(x.pack()).add(x));

        return map;
    }

    @Override
    public Map<Category, List<Song>> categorySongListMap() {
        Map<Category, List<Song>> map = new LinkedHashMap<>();

        categoryMap.values().stream().sorted(Category::compareTo)
                .forEach(x -> map.put(x, new ArrayList<>()));

        songMap.values().stream().sorted(new SongPackComparator())
                .forEach(x -> map.get(x.pack().category()).add(x));

        return map;
    }
}
