package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Board;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.ApiException;
import com.github.johypark97.varchivemacro.lib.common.api.RecordFetcher.FailureJson;
import com.github.johypark97.varchivemacro.lib.common.api.RecordFetcher.SuccessJson;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
class RecordFetcherImplTest {
    private static final Gson gson = newGsonBuilder_general().create();

    private static final String dummyDjName = "__@__";
    private static final URI dummyUri = URI.create("https://test.uri");

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private RecordFetcherImpl recordFetcher;

    private OngoingStubbing<HttpResponse<String>> stubHttpClientSend() throws Exception {
        return when(httpClientMock.send(any(HttpRequest.class), eq(BodyHandlers.ofString())));
    }

    @BeforeEach
    void setup() {
        recordFetcher = new RecordFetcherImpl(httpClientMock, dummyDjName);
    }

    @Test
    void test_constructor() {
        assertThrows(NullPointerException.class, () -> new RecordFetcherImpl(httpClientMock, null));
        assertThrows(IllegalArgumentException.class,
                () -> new RecordFetcherImpl(httpClientMock, ""));
    }

    @Test
    void test_createUri() {
        String boardPlaceholder = "__board__";
        String buttonPlaceholder = "__button__";
        String encoded = URLEncoder.encode(dummyDjName, StandardCharsets.UTF_8);

        String urlFormat = "https://v-archive.net/api/archive/%s/board/%s/%s";
        String urlString = String.format(urlFormat, encoded, buttonPlaceholder, boardPlaceholder);

        URI expected = URI.create(urlString);
        URI actual = recordFetcher.createUri(buttonPlaceholder, boardPlaceholder);
        assertEquals(expected, actual);
    }

    @Test
    void test_createRequest_uri() {
        HttpRequest request = recordFetcher.createRequest(dummyUri);

        int expected = 0;
        int actual = request.uri().compareTo(dummyUri);
        assertEquals(expected, actual);
    }

    @Test
    void test_createRequest_method() {
        HttpRequest request = recordFetcher.createRequest(dummyUri);

        String expected = "GET";
        String actual = request.method();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 200)
    void test_fetch_200(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        SuccessJson data = new SuccessJson();
        data.success = true;

        when(httpResponseMock.body()).thenReturn(gson.toJson(data));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        recordFetcher.fetch(Button._4, Board._1);

        boolean condition = recordFetcher.getResult().success;
        assertTrue(condition);
    }

    @ParameterizedTest
    @ValueSource(ints = {404, 500})
    void test_fetch_404_500(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        String expected = "exception message" + statusCode;

        FailureJson data = new FailureJson();
        data.message = expected;

        when(httpResponseMock.body()).thenReturn(gson.toJson(data));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        Throwable throwable =
                assertThrows(ApiException.class, () -> recordFetcher.fetch(Button._4, Board._1));

        String actual = throwable.getMessage();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 503)
    void test_fetch_etc(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        assertThrows(ApiException.class, () -> recordFetcher.fetch(Button._4, Board._1));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 404, 500})
    void test_fetch_jsonSyntaxException(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        String data = "{";

        when(httpResponseMock.body()).thenReturn(data);
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        assertThrows(JsonSyntaxException.class, () -> recordFetcher.fetch(Button._4, Board._1));
    }

    @Test
    void test_fetch_ioException() throws Exception {
        stubHttpClientSend().thenThrow(new IOException());

        assertThrows(IOException.class, () -> recordFetcher.fetch(Button._4, Board._1));
    }

    @Test
    void test_fetch_interruptedException() throws Exception {
        stubHttpClientSend().thenThrow(new InterruptedException());

        assertThrows(InterruptedException.class, () -> recordFetcher.fetch(Button._4, Board._1));
    }
}
