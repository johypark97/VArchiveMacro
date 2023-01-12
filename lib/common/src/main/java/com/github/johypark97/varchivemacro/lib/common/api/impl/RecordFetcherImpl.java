package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.common.api.Board;
import com.github.johypark97.varchivemacro.lib.common.api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.RecordFetcher;
import com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher.Failure;
import com.github.johypark97.varchivemacro.lib.common.api.datastruct.recordfetcher.Success;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class RecordFetcherImpl implements RecordFetcher {
    private static final String URL_FORMAT = ImplBase.BASE_URL + "/api/archive/%s/board/%s/%s";

    private final Gson gson = newGsonBuilder_general().create();

    private Success result;
    private final HttpClient httpClient;
    private final String djName;

    public RecordFetcherImpl(HttpClient httpClient, String djName) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null");
        }
        if (djName == null) {
            throw new NullPointerException("djName is null");
        }
        if (djName.isBlank()) {
            throw new IllegalArgumentException("djName is blank");
        }

        this.httpClient = httpClient;
        this.djName = djName;
    }

    protected URI createUri(String button, String board) {
        String encoded = URLEncoder.encode(djName, StandardCharsets.UTF_8);
        String url = String.format(URL_FORMAT, encoded, button, board);
        return URI.create(url);
    }

    protected HttpRequest createRequest(URI uri) {
        return ImplBase.newHttpRequestBuilder(uri).GET().build();
    }

    @Override
    public Success getResult() {
        return result;
    }

    @Override
    public void fetch(Button button, Board board) throws IOException, InterruptedException {
        URI uri = createUri(button.toString(), board.toString());
        HttpRequest request = createRequest(uri);

        // TODO: Change to use sendAsync in final release.
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        switch (statusCode) {
            case 200 -> result = gson.fromJson(response.body(), Success.class);
            case 404, 500 -> {
                Failure failure = gson.fromJson(response.body(), Failure.class);
                throw new RuntimeException(failure.message);
            }
            default -> throw new RuntimeException(statusCode + " Network Error");
        }
    }
}
