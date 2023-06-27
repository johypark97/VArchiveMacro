package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.johypark97.varchivemacro.lib.common.api.StaticFetcher.RemoteSong;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
class StaticFetcherImplTest {
    private static final Gson gson = newGsonBuilder_general().create();

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    private StaticFetcherImpl staticFetcher;

    private OngoingStubbing<HttpResponse<String>> stubHttpClientSend() throws Exception {
        return when(httpClientMock.send(any(HttpRequest.class), eq(BodyHandlers.ofString())));
    }

    @BeforeEach
    void setup() {
        staticFetcher = new StaticFetcherImpl(httpClientMock);
    }

    @Test
    void test_constructor() {
        assertThrows(NullPointerException.class, () -> new StaticFetcherImpl(null));
    }

    @ParameterizedTest
    @ValueSource(ints = 200)
    void test_fetchSongs_200(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        RemoteSong song1 = new RemoteSong();
        song1.title = "song 1";

        RemoteSong song2 = new RemoteSong();
        song2.title = "song 2";

        List<RemoteSong> data = new ArrayList<>();
        data.add(song1);
        data.add(song2);

        when(httpResponseMock.body()).thenReturn(gson.toJson(data));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        staticFetcher.fetchSongs();

        int expected = 2;
        int actual = staticFetcher.getSongs().size();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 404)
    void test_fetchSongs_etc(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        staticFetcher.fetchSongs();

        int expected = 0;
        int actual = staticFetcher.getSongs().size();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 200)
    void test_fetchSongs_jsonSyntaxException(int statusCode) throws Exception {
        stubHttpClientSend().thenReturn(httpResponseMock);

        String data = "{";

        when(httpResponseMock.body()).thenReturn(data);
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        assertThrows(JsonSyntaxException.class, () -> staticFetcher.fetchSongs());
    }

    @Test
    void test_fetchSongs_ioException() throws Exception {
        stubHttpClientSend().thenThrow(new IOException());

        assertThrows(IOException.class, () -> staticFetcher.fetchSongs());
    }

    @Test
    void test_fetchSongs_interruptedException() throws Exception {
        stubHttpClientSend().thenThrow(new InterruptedException());

        assertThrows(InterruptedException.class, () -> staticFetcher.fetchSongs());
    }
}
