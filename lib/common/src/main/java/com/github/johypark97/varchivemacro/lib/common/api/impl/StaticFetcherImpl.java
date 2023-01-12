package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.datastruct.staticfetcher.RemoteSong;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class StaticFetcherImpl implements StaticFetcher {
    private static final String SONGS_URL = ImplBase.BASE_URL + "/db/songs.json";

    private final Gson gson = newGsonBuilder_general().create();

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

        remoteSongs = (response.statusCode() == 200) ? gson.fromJson(response.body(),
                new RemoteSong.GsonListTypeToken()) : List.of();
    }
}
