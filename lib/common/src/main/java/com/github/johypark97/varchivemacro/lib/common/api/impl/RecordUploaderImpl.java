package com.github.johypark97.varchivemacro.lib.common.api.impl;

import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;

public class RecordUploaderImpl implements RecordUploader {
    private static final String URL_FORMAT = ImplBase.BASE_URL + "/client/open/%d/score";

    private boolean result;
    private final HttpClient httpClient;
    private final UUID token;
    private final int userNo;

    public RecordUploaderImpl(HttpClient httpClient, int userNo, UUID token) {
        if (httpClient == null) {
            throw new NullPointerException("httpClient is null");
        }
        if (userNo <= 0) {
            throw new IllegalArgumentException("userNo must greater than 0");
        }
        if (token == null) {
            throw new NullPointerException("token is null");
        }

        this.httpClient = httpClient;
        this.userNo = userNo;
        this.token = token;
    }

    protected URI createUri(int userNo) {
        String url = String.format(URL_FORMAT, userNo);
        return URI.create(url);
    }

    protected HttpRequest createRequest(URI uri, String data) {
        return ImplBase.newHttpRequestBuilder(uri).headers("Authorization", token.toString())
                .POST(BodyPublishers.ofString(data)).build();
    }

    @Override
    public boolean getResult() {
        return result;
    }

    @Override
    public void upload(RequestData data) throws IOException, InterruptedException {
        URI uri = createUri(userNo);
        HttpRequest request = createRequest(uri, data.toJson());

        // TODO: Change to use sendAsync in final release.
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
        int statusCode = response.statusCode();
        switch (statusCode) {
            case 200 -> {
                Success success = Success.fromJson(response.body());
                result = success.update;
            }
            case 400, 404, 500 -> {
                Failure failure = Failure.fromJson(response.body());
                throw new RuntimeException(failure.message);
            }
            default -> throw new RuntimeException(statusCode + " Network Error");
        }
    }
}
