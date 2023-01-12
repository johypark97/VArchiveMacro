package com.github.johypark97.varchivemacro.lib.common.api.impl;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ImplBaseTest {
    @Test
    void test_newHttpRequestBuilder_headers() {
        URI dummyUri = URI.create("https://test.uri");
        HttpRequest request = ImplBase.newHttpRequestBuilder(dummyUri).build();

        Map<String, List<String>> expectedMap =
                Map.ofEntries(Map.entry("Content-Type", List.of("application/json")));

        Set<Entry<String, List<String>>> expected = expectedMap.entrySet();
        Set<Entry<String, List<String>>> actual = request.headers().map().entrySet();
        assertIterableEquals(expected, actual);
    }
}
