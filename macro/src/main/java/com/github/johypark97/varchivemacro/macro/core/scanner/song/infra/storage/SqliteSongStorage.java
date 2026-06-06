package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.storage;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.sqlite.SQLiteConfig;

public class SqliteSongStorage implements SongStorage {
    private final String connectionUrl;

    public SqliteSongStorage(Path songDatabaseFilePath) {
        connectionUrl = "jdbc:sqlite:" + songDatabaseFilePath;
    }

    @Override
    public List<Song> load() throws SQLException {
        List<Song> songList = new LinkedList<>();

        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(true);

        try (Connection connection = config.createConnection(connectionUrl);
                Statement statement = connection.createStatement()) {
            // query category
            Map<String, Category> categoryMap = new HashMap<>(); // name - category
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Category")) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int priority = resultSet.getInt("priority");

                    categoryMap.put(name, new Category(name, priority));
                }
            }

            // query pack
            Map<String, Pack> packMap = new HashMap<>(); // name - pack
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM Pack")) {
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int priority = resultSet.getInt("priority");
                    String category = resultSet.getString("category");

                    packMap.put(name, new Pack(name, priority, categoryMap.get(category)));
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

                    songList.add(new Song(id, title, composer, priority, packMap.get(pack)));
                }
            }
        }

        return songList;
    }
}
