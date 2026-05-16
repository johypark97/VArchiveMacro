package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.converter;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Category;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Pack;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;

public class SongDatabaseConverter {
    public static Song.Pack.Category toDomainCategory(Category category) {
        return new Song.Pack.Category(category.name(), category.priority());
    }

    public static Song.Pack toDomainPack(Pack pack) {
        return new Song.Pack(pack.name(), pack.priority(), toDomainCategory(pack.category()));
    }

    public static Song toDomainSong(com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Song song) {
        return new Song(song.id(), song.title(), song.composer(), song.priority(),
                toDomainPack(song.pack()));
    }
}
