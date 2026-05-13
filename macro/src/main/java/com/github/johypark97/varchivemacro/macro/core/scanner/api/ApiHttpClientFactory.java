package com.github.johypark97.varchivemacro.macro.core.scanner.api;

import java.net.http.HttpClient;

public class ApiHttpClientFactory {
    public static HttpClient create() {
        return HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }
}
