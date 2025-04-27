package com.github.johypark97.varchivemacro.macro.infrastructure.scanner.loader;

import com.github.johypark97.varchivemacro.lib.scanner.database.DefaultRecordManager;
import java.nio.file.Path;

public interface SongRecordRepositoryLoader {
    DefaultRecordManager load() throws Exception;

    Path getSavePath();
}
