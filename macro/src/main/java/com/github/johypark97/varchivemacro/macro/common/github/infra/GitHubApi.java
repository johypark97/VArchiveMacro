package com.github.johypark97.varchivemacro.macro.common.github.infra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitHubApi implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubApi.class);

    protected final HttpClient httpClient;

    public GitHubApi() {
        this(Duration.ofSeconds(5));
    }

    public GitHubApi(Duration timeout) {
        httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    public HttpResponse<String> request(URI uri) throws IOException, InterruptedException {
        LOGGER.atDebug().log("GitHubApi request: {}", uri);

        HttpRequest.Builder builder = HttpRequest.newBuilder();

        builder.header("Accept", "application/vnd.github+json");
        builder.header("X-GitHub-Api-Version", "2022-11-28");
        builder.uri(uri);

        HttpRequest request = builder.build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void close() {
        httpClient.close();
    }
}
