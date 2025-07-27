package com.github.johypark97.varchivemacro.macro.common.github.domain;

public record GitHubRelease(String htmlUrl, String tagName, String name, boolean draft,
                            boolean prerelease) {
    public Semver version() {
        return Semver.from(tagName);
    }
}
