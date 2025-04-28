package com.github.johypark97.varchivemacro.macro.infrastructure.songtitle;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultTitleTool;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.Song;
import java.io.IOException;
import java.nio.file.Path;

public class SongTitleMapper {
    private final TitleTool titleTool;

    public SongTitleMapper(Path path) throws IOException {
        titleTool = new DefaultTitleTool(path);
    }

    public String getClippedTitleOrDefault(Song song) {
        return titleTool.getClippedTitleOrDefault(convertSong(song));
    }

    public String getRemoteTitleOrDefault(Song song) {
        return titleTool.getRemoteTitleOrDefault(convertSong(song));
    }

    public String remapScannedTitle(String value) {
        return titleTool.remapScannedTitle(value);
    }

    // TODO: Temporary converting method. Needs to refactoring.
    protected SongDatabase.Song convertSong(Song song) {
        return new SongDatabase.Song(song.songId(), song.title(), null, 0, null);
    }
}
