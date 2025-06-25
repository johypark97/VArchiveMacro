package com.github.johypark97.varchivemacro.macro.common.github.infra.fetcher;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.common.github.infra.api.GitHubApi;
import com.github.johypark97.varchivemacro.macro.common.github.infra.model.GitHubRelease;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpResponse;

public class LatestReleaseFetcher {
    public static GitHubRelease fetch(GitHubApi api, String owner, String repository)
            throws IOException, InterruptedException {
        URI uri = GitHubApi.UriBuilder.create_latestRelease(owner, repository);

        HttpResponse<String> httpResponse = api.send(uri);
        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        return gson.fromJson(httpResponse.body(), GitHubRelease.class);
    }
}
