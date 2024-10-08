package com.github.johypark97.varchivemacro.macro.api;

import com.github.johypark97.varchivemacro.lib.common.GsonWrapper;
import com.github.johypark97.varchivemacro.macro.api.data.GitHubRelease;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class VersionChecker {
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final String GITHUB_API_URL =
            "https://api.github.com/repos/johypark97/VArchiveMacro/releases/latest";

    private GitHubRelease latestRelease;

    public static HttpClient newHttpClient() {
        HttpClient.Builder builder = HttpClient.newBuilder();

        builder.connectTimeout(TIMEOUT);

        return builder.build();
    }

    public static HttpRequest newHttpRequest(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder();

        builder.header("Accept", "application/vnd.github+json");
        builder.header("X-GitHub-Api-Version", "2022-11-28");
        builder.uri(uri);

        return builder.build();
    }

    public static GitHubRelease fetchLatestRelease(URI uri)
            throws IOException, InterruptedException {
        HttpResponse<String> httpResponse =
                newHttpClient().send(newHttpRequest(uri), BodyHandlers.ofString());

        if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        Gson gson = GsonWrapper.newGsonBuilder_general().create();
        return gson.fromJson(httpResponse.body(), GitHubRelease.class);
    }

    public void fetch() throws IOException, InterruptedException {
        try {
            latestRelease = fetchLatestRelease(new URI(GITHUB_API_URL));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUpdated(String version) {
        if (latestRelease == null) {
            return false;
        }

        Semver currentVersion = new Semver(version);
        Semver latestVersion = new Semver(latestRelease.tagName());

        return Semver.compare(currentVersion, latestVersion) < 0;
    }

    public GitHubRelease getLatestRelease() {
        return latestRelease;
    }
}
