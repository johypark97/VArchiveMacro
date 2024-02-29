package com.github.johypark97.varchivemacro.lib.scanner.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.github.johypark97.varchivemacro.lib.scanner.api.impl.RecordFetcherImpl;
import com.github.johypark97.varchivemacro.lib.scanner.api.impl.RecordUploaderImpl;
import com.github.johypark97.varchivemacro.lib.scanner.api.impl.StaticFetcherImpl;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ApiTest {
    @Test
    void test_newHttpClient_tls() throws Exception {
        HttpClient client = Api.newHttpClient();

        String expected = "TLSv1.2";
        String actual = client.sslContext().getProtocol();
        assertEquals(expected, actual);
    }

    @Test
    void test_newHttpClient_version() throws Exception {
        HttpClient client = Api.newHttpClient();

        HttpClient.Version expected = Version.HTTP_2;
        HttpClient.Version actual = client.version();
        assertEquals(expected, actual);
    }

    @Test
    void test_newRecordFetcher() throws Exception {
        assertInstanceOf(RecordFetcherImpl.class, Api.newRecordFetcher("name"));
    }

    @Test
    void test_newRecordUploader() throws Exception {
        assertInstanceOf(RecordUploaderImpl.class, Api.newRecordUploader(1, UUID.randomUUID()));
    }

    @Test
    void test_newStaticFetcher() throws Exception {
        assertInstanceOf(StaticFetcherImpl.class, Api.newStaticFetcher());
    }
}
