package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.converter;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;

public class SongDatabaseConverter {
    public static Song.Pack.Category toDomainCategory(SongDatabase.Category category) {
        return new Song.Pack.Category(category.name(), category.priority());
    }

    public static Song.Pack toDomainPack(SongDatabase.Pack pack) {
        return new Song.Pack(pack.name(), pack.priority(), toDomainCategory(pack.category()));
    }

    public static Song toDomainSong(SongDatabase.Song song) {
        return new Song(song.id(), song.title(), song.composer(), song.priority(),
                toDomainPack(song.pack()));
    }
}
