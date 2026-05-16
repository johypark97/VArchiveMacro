package com.github.johypark97.varchivemacro.macro.core.scanner.title.infra;

import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
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
    protected com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Song convertSong(Song song) {
        return new com.github.johypark97.varchivemacro.macro.core.scanner.song.infra.model.Song(song.songId(), song.title(), null, 0, null);
    }
}
