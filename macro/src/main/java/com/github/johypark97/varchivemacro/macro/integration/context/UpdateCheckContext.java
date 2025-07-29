package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.ProgramDataService;
import com.github.johypark97.varchivemacro.macro.integration.app.app.ProgramVersionService;
import com.github.johypark97.varchivemacro.macro.integration.app.app.VersionData;
import java.nio.file.Path;

public class UpdateCheckContext implements Context {
    // constants
    private final Path PROGRAM_DATA_DIRECTORY_PATH = Path.of("data");
    private final String GITHUB_OWNER = "johypark97";
    private final String GITHUB_REPOSITORY = "VArchiveMacro";

    // states
    private final VersionData versionData = new VersionData();

    // services
    private final GitHubApiService gitHubApiService =
            new GitHubApiService(GITHUB_OWNER, GITHUB_REPOSITORY);
    private final ProgramDataService programDataService =
            new ProgramDataService(gitHubApiService, PROGRAM_DATA_DIRECTORY_PATH);

    // integrations
    public final ProgramVersionService programVersionService;

    public UpdateCheckContext(GlobalContext globalContext) {
        programVersionService =
                new ProgramVersionService(globalContext.configService, gitHubApiService,
                        programDataService, versionData);
    }
}
