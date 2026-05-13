package com.github.johypark97.varchivemacro.macro.core.scanner.api;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Button;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ApiUriCreator {
    private final URI BASE_URI;

    public ApiUriCreator(String baseUri) {
        BASE_URI = URI.create(baseUri);
    }

    public URI createRecordFetchUri(String djName, Button button) {
        String encodedDjName = URLEncoder.encode(djName, StandardCharsets.UTF_8);

        return BASE_URI.resolve("api/v2/archive/%s/button/%d".formatted(encodedDjName,
                button.toInt()));
    }

    public URI createRecordUploadUri(int userNo) {
        if (userNo <= 0) {
            throw new IllegalArgumentException("userNo must be a positive integer");
        }

        return BASE_URI.resolve("client/open/%d/score".formatted(userNo));
    }
}
