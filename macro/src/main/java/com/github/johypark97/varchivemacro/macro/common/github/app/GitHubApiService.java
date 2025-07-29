package com.github.johypark97.varchivemacro.macro.common.github.app;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubContent;
import com.github.johypark97.varchivemacro.macro.common.github.domain.GitHubRelease;
import com.github.johypark97.varchivemacro.macro.common.github.infra.GitHubApi;
import com.github.johypark97.varchivemacro.macro.common.github.infra.GitHubApiUriBuilder;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubContentJson;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubReleaseJson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

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
        return gson.fromJson(
                fetchString(GitHubApiUriBuilder.create_latestRelease(owner, repository)),
                GitHubReleaseJson.class).toDomain();
    }

    public List<GitHubRelease> fetchAllReleases() throws IOException, InterruptedException {
        class GsonTypeToken extends TypeToken<List<GitHubReleaseJson>> {
        }

        return gson.fromJson(fetchString(GitHubApiUriBuilder.create_releaseList(owner, repository)),
                new GsonTypeToken()).stream().map(GitHubReleaseJson::toDomain).toList();
    }

    public GitHubContent fetchContent(String path) throws IOException, InterruptedException {
        return gson.fromJson(
                fetchString(GitHubApiUriBuilder.create_content(owner, repository, path)),
                GitHubContentJson.class).toDomain();
    }

    private String fetchString(URI uri) throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = api.request(uri);
        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("[%d] %s", httpResponse.statusCode(), httpResponse.body()));
        }

        return httpResponse.body();
    }
}
