package com.github.johypark97.varchivemacro.macro.infrastructure.github;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.infrastructure.github.data.GitHubRelease;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpResponse;

public class GitHubRequest {
    private static final String owner = "johypark97";
    private static final String repository = "VArchiveMacro";

    public static String getContent(GitHubApi api, String path)
            throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = api.send_content(owner, repository, path);

        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(
                    String.format("[%d] %s", httpResponse.statusCode(), httpResponse.body()));
        }

        return httpResponse.statusCode() == HttpURLConnection.HTTP_OK ? httpResponse.body() : "";
    }

    public static GitHubRelease getLatestRelease(GitHubApi api)
            throws IOException, InterruptedException {
        HttpResponse<String> httpResponse = api.send_latestRelease(owner, repository);

        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        return gson.fromJson(httpResponse.body(), GitHubRelease.class);
    }
}
