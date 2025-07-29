package com.github.johypark97.varchivemacro.macro.common.github.domain;

import com.github.zafarkhaja.semver.Version;

public record GitHubRelease(String htmlUrl, String tagName, String name, boolean draft,
                            boolean prerelease) {
    public Version version() {
        // if the first letter is 'v', remove it
        String semver = tagName.startsWith("v") ? tagName.substring(1) : tagName;

        return Version.tryParse(semver).orElse(Version.of(0, 1, 0));
    }
}
