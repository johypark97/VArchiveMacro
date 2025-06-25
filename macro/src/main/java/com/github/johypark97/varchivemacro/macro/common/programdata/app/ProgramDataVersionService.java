package com.github.johypark97.varchivemacro.macro.common.programdata.app;

import com.github.johypark97.varchivemacro.macro.common.programdata.infra.updater.DataUpdater;
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
