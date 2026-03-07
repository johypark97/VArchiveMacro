package com.github.johypark97.varchivemacro.macro.common.github.infra.model;

import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.time.ZonedDateTime;

public record GitHubReleaseJson(@Expose @SerializedName("html_url") String htmlUrl,
                                @Expose @SerializedName("tag_name") String tagName,
                                @Expose String name,
                                @Expose boolean draft,
                                @Expose boolean prerelease,
                                @Expose @SerializedName("created_at") String createdAt,
                                @Expose @SerializedName("published_at") String publishedAt,
                                @Expose @SerializedName("updated_at") String updatedAt) {
    public GitHubRelease toDomain() {
        return new GitHubRelease(htmlUrl, tagName, name, draft, prerelease,
                ZonedDateTime.parse(createdAt), ZonedDateTime.parse(publishedAt),
                ZonedDateTime.parse(updatedAt));
    }
}
