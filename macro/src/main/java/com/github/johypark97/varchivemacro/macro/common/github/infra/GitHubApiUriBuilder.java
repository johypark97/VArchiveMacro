package com.github.johypark97.varchivemacro.macro.common.github.infra;

import java.net.URI;

public class GitHubApiUriBuilder {
    public static final String GITHUB_API_URL = "https://api.github.com/repos";

    private final StringBuilder builder = new StringBuilder(GITHUB_API_URL); // NOPMD

    public GitHubApiUriBuilder(String owner, String repository) {
        append(owner);
        append(repository);
    }

    public static URI create_content(String owner, String repository, String path) {
        return new GitHubApiUriBuilder(owner, repository).append("contents").append(path).build();
    }

    public static URI create_latestRelease(String owner, String repository) {
        return new GitHubApiUriBuilder(owner, repository).append("releases/latest").build();
    }

    public final GitHubApiUriBuilder append(String value) {
        builder.append('/').append(value);

        return this;
    }

    public URI build() {
        return URI.create(builder.toString());
    }
}
