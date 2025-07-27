package com.github.johypark97.varchivemacro.macro.common.github.app;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubContent;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.common.github.infra.GitHubApi;
import com.github.johypark97.varchivemacro.macro.common.github.infra.GitHubApiUriBuilder;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubContentJson;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubReleaseJson;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

public class GitHubApiService {
    private final GitHubApi api = new GitHubApi();
    private final Gson gson = GsonWrapper.newGsonBuilder_general().create();

    private final String owner;
    private final String repository;

    public GitHubApiService(String owner, String repository) {
        this.owner = owner;
        this.repository = repository;
    }

    public GitHubRelease fetchLatestRelease() throws IOException, InterruptedException {
        return fetchString(GitHubReleaseJson.class,
                GitHubApiUriBuilder.create_latestRelease(owner, repository)).toDomain();
    }

    public GitHubContent fetchContent(String path) throws IOException, InterruptedException {
        return fetchString(GitHubContentJson.class,
                GitHubApiUriBuilder.create_content(owner, repository, path)).toDomain();
    }

    private <T> T fetchString(Class<T> cls, URI uri) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = api.request(uri);
        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("[%d] %s", httpResponse.statusCode(), httpResponse.body()));
        }

        return gson.fromJson(httpResponse.body(), cls);
    }
}
