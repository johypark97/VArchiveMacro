package com.github.johypark97.varchivemacro.macro.integration.app.app;

import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.github.app.GitHubApiService;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.ProgramDataService;
import com.github.johypark97.varchivemacro.macro.common.programdata.app.UpdateProgressHook;
import com.github.johypark97.varchivemacro.macro.common.programdata.domain.DataVersion;
import com.github.johypark97.varchivemacro.macro.common.resource.BuildInfo;
import com.github.zafarkhaja.semver.Version;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramVersionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramVersionService.class);

    private final ConfigService configService;
    private final GitHubApiService gitHubApiService;
    private final ProgramDataService programDataService;

    private final VersionData latestVersionData;

    public ProgramVersionService(ConfigService configService, GitHubApiService gitHubApiService,
            ProgramDataService programDataService, VersionData latestVersionData) {
        this.configService = configService;
        this.gitHubApiService = gitHubApiService;
        this.programDataService = programDataService;

        this.latestVersionData = latestVersionData;
    }

    public void fetchLatestRelease() throws IOException, InterruptedException {
        boolean prereleaseNotification = configService.findProgramConfig().prereleaseNotification();

        GitHubRelease release = null;

        if (prereleaseNotification) {
            release = gitHubApiService.fetchLatestRelease();
        } else {
            List<GitHubRelease> list = gitHubApiService.fetchAllReleases();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("All fetched releases' version: {}", list.stream()
                        .map(x -> String.format("%s (%s)", x.version(), x.prerelease())).toList());
            }

            for (GitHubRelease x : list) {
                if (!x.prerelease()) {
                    release = x;
                    break;
                }
            }
        }

        if (release == null) {
            throw new RuntimeException("No release found.");
        }

        LOGGER.atTrace().log("Found latest release: {} (isPrerelease: {})", release.version(),
                release.prerelease());

        latestVersionData.setLatestGitHubRelease(release);
    }

    public Optional<Version> getLatestProgramVersion() {
        return Optional.ofNullable(latestVersionData.getLatestGitHubRelease())
                .map(GitHubRelease::version);
    }

    public Optional<String> getLatestReleaseHtmlUrl() {
        return Optional.ofNullable(latestVersionData.getLatestGitHubRelease())
                .map(GitHubRelease::htmlUrl);
    }

    public Optional<Boolean> isLatestReleasePrerelease() {
        return Optional.ofNullable(latestVersionData.getLatestGitHubRelease())
                .map(GitHubRelease::prerelease);
    }

    public Version getProgramVersion() {
        return Version.parse(BuildInfo.version);
    }

    public boolean isNewVersionReleased() {
        return getLatestProgramVersion().map(x -> x.isHigherThan(getProgramVersion()))
                .orElse(false);
    }

    public void fetchLatestProgramDataVersion() throws IOException, InterruptedException {
        latestVersionData.setLatestProgramDataVersion(programDataService.fetchLatestVersion());
    }

    public ZonedDateTime getLatestProgramDataVersion() {
        DataVersion x = latestVersionData.getLatestProgramDataVersion();
        return Optional.ofNullable(x).map(DataVersion::version)
                .orElse(ZonedDateTime.of(LocalDateTime.MIN, ZoneOffset.UTC));
    }

    public ZonedDateTime getProgramDataVersion() throws IOException {
        return programDataService.readLocalVersion().version();
    }

    public boolean isProgramDataUpdated() throws IOException {
        return getLatestProgramDataVersion().isAfter(getProgramDataVersion());
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
