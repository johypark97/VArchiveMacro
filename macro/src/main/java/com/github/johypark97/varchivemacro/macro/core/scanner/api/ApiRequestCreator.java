package com.github.johypark97.varchivemacro.macro.core.scanner.api;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.account.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.Button;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Objects;

public class ApiRequestCreator {
    private ApiUriCreator apiUriCreator;

    public ApiRequestCreator(ApiUriCreator apiUriCreator) {
        setApiUriCreator(apiUriCreator);
    }

    public final void setApiUriCreator(ApiUriCreator apiUriCreator) {
        this.apiUriCreator = Objects.requireNonNull(apiUriCreator);
    }

    public HttpRequest createRecordFetchRequest(String djName, Button button) {
        URI uri = apiUriCreator.createRecordFetchUri(djName, button);

        return createBuilder(uri).GET().build();
    }

    public HttpRequest createRecordUploadRequest(Account account, String data) {
        URI uri = apiUriCreator.createRecordUploadUri(account.userNo());

        return createBuilder(uri).headers("Authorization", account.token().toString())
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();
    }

    private HttpRequest.Builder createBuilder(URI uri) {
        return HttpRequest.newBuilder(uri).headers("Content-Type", "application/json");
    }
}
