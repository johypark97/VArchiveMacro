package com.github.johypark97.varchivemacro.macro.common.github.infra.model;

import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubContent;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public record GitHubContentJson(
        // @formatter:off
        @Expose @SerializedName("download_url") String downloadUrl,
        @Expose String content,
        @Expose String name,
        @Expose String type
        // @formatter:on
) {
    public GitHubContent toDomain() {
        return new GitHubContent(downloadUrl, content, name, type);
    }
}
