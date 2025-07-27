package com.github.johypark97.varchivemacro.macro.common.programdata.app;

import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.DataUpdater;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.VersionFetcher;
import java.io.IOException;
import java.nio.file.Path;

public class ProgramDataService {
    private static final String DATA_VERSION_FILENAME = "version.json";

    private final DataUpdater dataUpdater;
    private final VersionFetcher versionFetcher;

    public ProgramDataService(GitHubApiService gitHubApiService, Path dataDirectoryPath) {
        dataUpdater = new DataUpdater(gitHubApiService, dataDirectoryPath, DATA_VERSION_FILENAME);
        versionFetcher =
                new VersionFetcher(gitHubApiService, dataDirectoryPath, DATA_VERSION_FILENAME);
    }

    public DataVersion fetchLatestVersion() throws IOException, InterruptedException {
        return versionFetcher.fetchRemoteDataVersion();
    }

    public DataVersion readLocalVersion() throws IOException {
        return versionFetcher.readLocalDataVersion();
    }

    public void updateData(DataVersion latestDataVersion, UpdateProgressHook hook)
            throws IOException, InterruptedException {
        dataUpdater.update(latestDataVersion, hook);
    }
}
