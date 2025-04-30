package com.github.johypark97.varchivemacro.macro.infrastructure.github.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public record GitHubRelease(@Expose @SerializedName("html_url") String htmlUrl,
                            @Expose @SerializedName("tag_name") String tagName, @Expose String name,
                            @Expose boolean draft, @Expose boolean prerelease) {
    public Semver version() {
        return Semver.from(tagName);
    }
}
