package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.Failure;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestData;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.Success;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;

@ExtendWith(MockitoExtension.class)
class RecordUploaderImplTest {
    private static final Gson gson = newGsonBuilder_general().create();

    private static final String dummyRequestString = "test data";
    private static final URI dummyUri = URI.create("https://test.uri");
    private static final UUID dummyUuid = UUID.randomUUID();
    private static final int dummyUserNo = 1;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    @Mock
    private RequestData dummyRequestData;

    private RecordUploaderImpl recordUploader;

    @BeforeEach
    void setup() {
        recordUploader = new RecordUploaderImpl(httpClientMock, dummyUserNo, dummyUuid);
    }

    @Test
    void test_constructor() {
        assertThrows(IllegalArgumentException.class,
                () -> new RecordUploaderImpl(httpClientMock, 0, null));
        assertThrows(NullPointerException.class,
                () -> new RecordUploaderImpl(httpClientMock, 1, null));
    }

    @ParameterizedTest
    @ValueSource(ints = 0)
    void test_createUri(int value) {
        String urlFormat = "https://v-archive.net/client/open/%d/score";
        String urlString = String.format(urlFormat, value);

        URI expected = URI.create(urlString);
        URI actual = recordUploader.createUri(value);
        assertEquals(expected, actual);
    }

    @Test
    void test_createRequest_uri() {
        HttpRequest request = recordUploader.createRequest(dummyUri, dummyRequestString);

        int expected = 0;
        int acutal = request.uri().compareTo(dummyUri);
        assertEquals(expected, acutal);
    }

    @Test
    void test_createRequest_method() {
        HttpRequest request = recordUploader.createRequest(dummyUri, dummyRequestString);

        String expected = "POST";
        String actual = request.method();
        assertEquals(expected, actual);
    }

    @Test
    void test_createRequest_headers() {
        HttpRequest request = recordUploader.createRequest(dummyUri, dummyRequestString);

        Map<String, List<String>> expectedMap =
                Map.ofEntries(Map.entry("Content-Type", List.of("application/json")),
                        Map.entry("Authorization", List.of(dummyUuid.toString())));

        Set<Entry<String, List<String>>> expected = expectedMap.entrySet();
        Set<Entry<String, List<String>>> actual = request.headers().map().entrySet();
        assertEquals(expected, actual);
    }

    @Test
    void test_createRequest_requestData() {
        HttpRequest request = recordUploader.createRequest(dummyUri, dummyRequestString);

        assertTrue(request.bodyPublisher().isPresent());

        long expected = dummyRequestString.length();
        long actual = request.bodyPublisher().get().contentLength();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 200)
    void test_upload_200(int statusCode) throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenReturn(httpResponseMock);

        Success data = new Success();
        data.update = false;

        when(httpResponseMock.body()).thenReturn(gson.toJson(data));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        recordUploader.upload(dummyRequestData);

        boolean condition = recordUploader.getResult();
        assertFalse(condition);
    }

    @ParameterizedTest
    @ValueSource(ints = 200)
    void test_upload_200_newRecord(int statusCode) throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenReturn(httpResponseMock);

        Success data = new Success();
        data.update = true;

        when(httpResponseMock.body()).thenReturn(gson.toJson(data));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        recordUploader.upload(dummyRequestData);

        boolean condition = recordUploader.getResult();
        assertTrue(condition);
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 500})
    void test_upload_400_404_500(int statusCode) throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenReturn(httpResponseMock);

        String expected = "exception message" + statusCode;

        Failure failure = new Failure();
        failure.message = expected;

        when(httpResponseMock.body()).thenReturn(gson.toJson(failure));
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        Throwable throwable =
                assertThrows(RuntimeException.class, () -> recordUploader.upload(dummyRequestData));

        String actual = throwable.getMessage();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = 503)
    void test_upload_etc(int statusCode) throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenReturn(httpResponseMock);

        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        assertThrows(RuntimeException.class, () -> recordUploader.upload(dummyRequestData));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 400, 404, 500})
    void test_upload_jsonSyntaxException(int statusCode) throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenReturn(httpResponseMock);

        String data = "{";

        when(httpResponseMock.body()).thenReturn(data);
        when(httpResponseMock.statusCode()).thenReturn(statusCode);

        assertThrows(JsonSyntaxException.class, () -> recordUploader.upload(dummyRequestData));
    }

    @Test
    void test_upload_ioException() throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenThrow(new IOException());

        assertThrows(IOException.class, () -> recordUploader.upload(dummyRequestData));
    }

    @Test
    void test_upload_interruptedException() throws Exception {
        stubDummyRequestData();
        stubHttpClientSend().thenThrow(new InterruptedException());

        assertThrows(InterruptedException.class, () -> recordUploader.upload(dummyRequestData));
    }

    private void stubDummyRequestData() {
        when(dummyRequestData.toJson()).thenReturn("");
    }

    private OngoingStubbing<HttpResponse<String>> stubHttpClientSend() throws Exception {
        return when(httpClientMock.send(any(HttpRequest.class), eq(BodyHandlers.ofString())));
    }
}
