package com.github.johypark97.varchivemacro.macro.core.scanner.title.app;

import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleMapper;
import java.io.IOException;
import java.nio.file.Path;

public class SongTitleService {
    private final SongTitleMapper songTitleMapper;

    public SongTitleService(Path path) throws IOException {
        songTitleMapper = new SongTitleMapper(path);
    }

    public String getClippedTitleOrDefault(Song song) {
        return songTitleMapper.getClippedTitleOrDefault(song);
    }

    public String getRemoteTitleOrDefault(Song song) {
        return songTitleMapper.getRemoteTitleOrDefault(song);
    }

    public String remapScannedTitle(String value) {
        return songTitleMapper.remapScannedTitle(value);
    }

    public String normalizeTitle(String value) {
        return TitleTool.normalizeTitle_recognition(value);
    }
}
