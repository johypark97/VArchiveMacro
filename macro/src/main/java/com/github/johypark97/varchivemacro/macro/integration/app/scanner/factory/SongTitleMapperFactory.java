package com.github.johypark97.varchivemacro.macro.integration.app.scanner.factory;

import com.github.johypark97.varchivemacro.macro.core.scanner.title.infra.SongTitleMapper;
import java.io.IOException;

public interface SongTitleMapperFactory {
    SongTitleMapper create() throws IOException;
}
