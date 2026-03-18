package com.github.johypark97.varchivemacro.lib.scanner.database;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface SongDatabase {
    Category getCategory(String name);

    Pack getPack(String name);

    Song getSong(int id);

    List<Category> categoryList();

    List<Pack> packList();

    List<Song> songList();

    Map<Pack, List<Song>> packSongListMap();

    Map<Category, List<Song>> categorySongListMap();

    record Category(String name, int priority) implements Comparable<Category> {
        @Override
        public int compareTo(Category o) {
            return priority - o.priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Category category = (Category) o;
            return Objects.equals(name, category.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }


    record Pack(String name, int priority, Category category) implements Comparable<Pack> {
        @Override
        public int compareTo(Pack o) {
            int x = category.compareTo(o.category);
            if (x != 0) {
                return x;
            }

            return priority - o.priority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Pack pack = (Pack) o;
            return Objects.equals(name, pack.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }


    record Song(int id, String title, String composer, int priority, Pack pack) {
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Song song = (Song) o;
            return id == song.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }
}
