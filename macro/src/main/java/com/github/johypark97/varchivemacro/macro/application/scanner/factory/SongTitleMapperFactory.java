package com.github.johypark97.varchivemacro.macro.application.scanner.factory;

import com.github.johypark97.varchivemacro.macro.infrastructure.songtitle.SongTitleMapper;
import java.io.IOException;

public interface SongTitleMapperFactory {
    SongTitleMapper create() throws IOException;
}
