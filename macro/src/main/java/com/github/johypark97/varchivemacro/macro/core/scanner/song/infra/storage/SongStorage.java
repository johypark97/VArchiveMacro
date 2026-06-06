package com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.storage;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.sql.SQLException;
import java.util.List;

public interface SongStorage {
    List<Song> load() throws SQLException;
}
