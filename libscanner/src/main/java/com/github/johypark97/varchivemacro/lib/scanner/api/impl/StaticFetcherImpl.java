package com.github.johypark97.varchivemacro.lib.scanner.api.impl;

import com.github.johypark97.varchivemacro.lib.scanner.api.StaticFetcher;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class StaticFetcherImpl implements StaticFetcher {
    private static final String SONGS_URL = ImplBase.BASE_URL + "/db/songs.json";

    private final HttpClient httpClient;
    public List<RemoteSong> remoteSongs;

    public StaticFetcherImpl(HttpClient httpClient) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null");
        }

        this.httpClient = httpClient;
    }

    @Override
    public List<RemoteSong> getSongs() {
        return remoteSongs;
    }

    @Override
    public void fetchSongs() throws IOException, InterruptedException {
        URI uri = URI.create(SONGS_URL);
        HttpRequest request = ImplBase.newHttpRequestBuilder(uri).GET().build();

        // TODO: Change to use sendAsync in final release.
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        remoteSongs =
                (response.statusCode() == 200) ? RemoteSong.fromJson(response.body()) : List.of();
    }
}
