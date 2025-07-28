package com.github.johypark97.varchivemacro.macro.common.programdata.infra;

import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubContent;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.programdata.infra.model.DataVersionJson;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;

public class VersionFetcher {
    private static final Path DATA_VERSION_REQUEST_BASE_PATH = Path.of("macro");

    private final GitHubApiService gitHubApiService;

    private final Path dataDirectoryPath;
    private final String dataVersionFilename;

    public VersionFetcher(GitHubApiService gitHubApiService, Path dataDirectoryPath,
            String dataVersionFilename) {
        this.gitHubApiService = gitHubApiService;

        this.dataDirectoryPath = dataDirectoryPath;
        this.dataVersionFilename = dataVersionFilename;
    }

    public DataVersion readLocalDataVersion() throws IOException {
        return DataVersionJson.from(dataDirectoryPath.resolve(dataVersionFilename)).toDomain();
    }

    public DataVersion fetchRemoteDataVersion() throws IOException, InterruptedException {
        String path = DATA_VERSION_REQUEST_BASE_PATH.resolve(dataDirectoryPath)
                .resolve(dataVersionFilename).toString().replace('\\', '/');

        GitHubContent content = gitHubApiService.fetchContent(path);
        byte[] data = Base64.getMimeDecoder().decode(content.content());

        return DataVersionJson.from(new String(data)).toDomain();
    }
}
