package com.github.johypark97.varchivemacro.macro.integration.app.app;

import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;

public class VersionData {
    private GitHubRelease latestGitHubRelease;
    private DataVersion latestProgramDataVersion;

    public GitHubRelease getLatestGitHubRelease() {
        return latestGitHubRelease;
    }

    public void setLatestGitHubRelease(GitHubRelease value) {
        latestGitHubRelease = value;
    }

    public DataVersion getLatestProgramDataVersion() {
        return latestProgramDataVersion;
    }

    public void setLatestProgramDataVersion(DataVersion value) {
        latestProgramDataVersion = value;
    }
}
