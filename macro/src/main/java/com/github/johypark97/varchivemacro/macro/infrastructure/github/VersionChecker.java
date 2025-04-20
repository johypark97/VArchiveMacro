package com.github.johypark97.varchivemacro.macro.infrastructure.github;

import com.github.johypark97.varchivemacro.macro.infrastructure.github.data.GitHubRelease;
import java.io.IOException;

public class VersionChecker {
    private GitHubRelease latestRelease;

    public void fetch(GitHubApi api) throws IOException, InterruptedException {
        latestRelease = GitHubRequest.getLatestRelease(api);
    }

    public boolean isUpdated(String version) {
        if (latestRelease == null) {
            return false;
        }

        Semver currentVersion = new Semver(version);
        Semver latestVersion = new Semver(latestRelease.tagName());

        return Semver.compare(currentVersion, latestVersion) < 0;
    }

    public GitHubRelease getLatestRelease() {
        return latestRelease;
    }
}
