package com.github.johypark97.varchivemacro.macro.infrastructure.github;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GitHubApi implements AutoCloseable {
    protected final HttpClient httpClient;

    public GitHubApi() {
        this(Duration.ofSeconds(5));
    }

    public GitHubApi(Duration timeout) {
        httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
    }

    public HttpResponse<String> send_content(String owner, String repository, String path)
            throws IOException, InterruptedException {
        return send(UriBuilder.create_content(owner, repository, path));
    }

    public HttpResponse<String> send_latestRelease(String owner, String repository)
            throws IOException, InterruptedException {
        return send(UriBuilder.create_latestRelease(owner, repository));
    }

    protected HttpResponse<String> send(URI uri) throws IOException, InterruptedException {
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

    public static class UriBuilder {
        public static final String GITHUB_API_URL = "https://api.github.com/repos";

        private final StringBuilder builder = new StringBuilder(GITHUB_API_URL); // NOPMD

        public UriBuilder(String owner, String repository) {
            append(owner);
            append(repository);
        }

        public static URI create_content(String owner, String repository, String path) {
            return new UriBuilder(owner, repository).append("contents").append(path).build();
        }

        public static URI create_latestRelease(String owner, String repository) {
            return new UriBuilder(owner, repository).append("releases/latest").build();
        }

        public URI build() {
            return URI.create(builder.toString());
        }

        protected final UriBuilder append(String value) {
            builder.append('/').append(value);

            return this;
        }
    }
}
