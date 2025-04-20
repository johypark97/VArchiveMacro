package com.github.johypark97.varchivemacro.macro.infrastructure.github.data;

import com.github.johypark97.varchivemacro.macro.infrastructure.github.Semver;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public record GitHubRelease(@Expose @SerializedName("html_url") String htmlUrl,
                            @Expose @SerializedName("tag_name") String tagName, @Expose String name,
                            @Expose boolean draft, @Expose boolean prerelease) {
    public String getVersion() {
        return new Semver(tagName).toString();
    }
}
