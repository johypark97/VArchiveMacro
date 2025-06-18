package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleMapper;
import java.io.IOException;
import java.nio.file.Path;

public class DefaultSongTitleMapperFactory implements SongTitleMapperFactory {
    private final Path path;

    public DefaultSongTitleMapperFactory(Path path) {
        this.path = path;
    }

    @Override
    public SongTitleMapper create() throws IOException {
        return new SongTitleMapper(path);
    }
}
