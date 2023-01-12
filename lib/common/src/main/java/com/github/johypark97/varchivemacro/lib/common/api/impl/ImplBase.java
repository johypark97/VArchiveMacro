package com.github.johypark97.varchivemacro.lib.common.api.impl;

import java.net.URI;
import java.net.http.HttpRequest;

final class ImplBase {
    public static final String BASE_URL = "https://v-archive.net";

    public static HttpRequest.Builder newHttpRequestBuilder(URI uri) {
        return HttpRequest.newBuilder(uri).headers("Content-Type", "application/json");
    }
}
