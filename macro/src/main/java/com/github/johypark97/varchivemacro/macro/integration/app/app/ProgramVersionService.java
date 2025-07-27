package com.github.johypark97.varchivemacro.macro.integration.app.app;

import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.common.github.domain.Semver;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.ProgramDataService;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.UpdateProgressHook;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.resource.BuildInfo;
import java.io.IOException;
import java.util.Optional;

public class ProgramVersionService {
    private final GitHubApiService gitHubApiService;
    private final ProgramDataService programDataService;

    private final VersionData latestVersionData;

    public ProgramVersionService(GitHubApiService gitHubApiService,
            ProgramDataService programDataService, VersionData latestVersionData) {
        this.gitHubApiService = gitHubApiService;
        this.programDataService = programDataService;

        this.latestVersionData = latestVersionData;
    }

    public void fetchLatestRelease() throws IOException, InterruptedException {
        latestVersionData.setLatestGitHubRelease(gitHubApiService.fetchLatestRelease());
    }

    public Optional<Semver> getLatestProgramVersion() {
        return Optional.ofNullable(latestVersionData.getLatestGitHubRelease())
                .map(GitHubRelease::version);
    }

    public Optional<String> getLatestReleaseHtmlUrl() {
        return Optional.ofNullable(latestVersionData.getLatestGitHubRelease())
                .map(GitHubRelease::htmlUrl);
    }

    public Semver getProgramVersion() {
        return Semver.from(BuildInfo.version);
    }

    public boolean isNewVersionReleased() {
        return getLatestProgramVersion().map(x -> x.compareTo(getProgramVersion()) > 0)
                .orElse(false);
    }

    public void fetchLatestProgramDataVersion() throws IOException, InterruptedException {
        latestVersionData.setLatestProgramDataVersion(programDataService.fetchLatestVersion());
    }

    public long getLatestProgramDataVersion() {
        DataVersion x = latestVersionData.getLatestProgramDataVersion();
        return Optional.ofNullable(x).map(DataVersion::version).orElse((long) -1);
    }

    public long getProgramDataVersion() throws IOException {
        return programDataService.readLocalVersion().version();
    }

    public boolean isProgramDataUpdated() throws IOException {
        return getLatestProgramDataVersion() > getProgramDataVersion();
    }

    public void updateProgramData(UpdateProgressHook hook)
            throws IOException, InterruptedException {
        DataVersion latestDataVersion = latestVersionData.getLatestProgramDataVersion();
        if (latestDataVersion == null) {
            throw new IllegalStateException(
                    "The latest program data version has not been fetched.");
        }

        programDataService.updateData(latestDataVersion, hook);
    }
}
