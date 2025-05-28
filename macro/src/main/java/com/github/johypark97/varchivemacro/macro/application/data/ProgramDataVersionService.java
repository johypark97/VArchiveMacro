package com.github.johypark97.varchivemacro.macro.application.data;

import com.github.johypark97.varchivemacro.macro.infrastructure.data.updater.DataUpdater;
import java.io.IOException;
import java.nio.file.Path;

public class ProgramDataVersionService {
    private final DataUpdater dataUpdater;

    public ProgramDataVersionService(Path dataDirectoryPath) {
        dataUpdater = new DataUpdater(dataDirectoryPath);
    }

    public Long getVersion() throws IOException {
        dataUpdater.loadLocalDataVersion();

        return dataUpdater.getCurrentVersion();
    }
}
