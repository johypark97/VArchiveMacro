package com.github.johypark97.varchivemacro.lib.scanner.api.impl;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.api.ApiException;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordFetcher;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class RecordFetcherImpl implements RecordFetcher {
    private static final String URL_FORMAT = ImplBase.BASE_URL + "/api/v2/archive/%s/button/%s";

    private final HttpClient httpClient;
    private final String djName;
    private SuccessJson result;

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

    protected URI createUri(String button) {
        String encoded = URLEncoder.encode(djName, StandardCharsets.UTF_8);
        String url = String.format(URL_FORMAT, encoded, button);
        return URI.create(url);
    }

    protected HttpRequest createRequest(URI uri) {
        return ImplBase.newHttpRequestBuilder(uri).GET().build();
    }

    @Override
    public SuccessJson getResult() {
        return result;
    }

    @Override
    public void fetch(Button button) throws IOException, InterruptedException, ApiException {
        URI uri = createUri(button.toString());
        HttpRequest request = createRequest(uri);

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        switch (statusCode) {
            case 200 -> result = SuccessJson.fromJson(response.body());
            case 400, 404, 500 -> {
                FailureJson failure = FailureJson.fromJson(response.body());
                throw new ApiException(failure.message);
            }
            default -> throw new ApiException("Network Error: " + statusCode);
        }
    }
}
