package com.github.johypark97.varchivemacro.macro.core.scanner.api.record;

import com.github.johypark97.varchivemacro.macro.core.scanner.api.ApiRequestCreator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.account.Account;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.exception.ApiException;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.fetch.FetchFailureJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.fetch.FetchSuccessJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.upload.UploadFailureJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.upload.UploadRequestJson;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.record.upload.UploadSuccessJson;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiRecordService {
    private final HttpClient httpClient;
    private final ApiRequestCreator apiRequestCreator;
    private final Gson gson;

    public ApiRecordService(HttpClient httpClient, ApiRequestCreator apiRequestCreator, Gson gson) {
        this.httpClient = httpClient;
        this.apiRequestCreator = apiRequestCreator;
        this.gson = gson;
    }

    public FetchSuccessJson fetch(String djName, Button button)
            throws IOException, InterruptedException, ApiException {
        HttpRequest request = apiRequestCreator.createRecordFetchRequest(djName, button);

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        return switch (statusCode) {
            case 200 -> gson.fromJson(response.body(), FetchSuccessJson.class);
            case 400, 404, 500 -> {
                FetchFailureJson failure = gson.fromJson(response.body(), FetchFailureJson.class);
                throw new ApiException(failure.message());
            }
            default -> throw new ApiException("Network Error: " + statusCode);
        };
    }

    public boolean upload(Account account, UploadRequestJson record)
            throws IOException, InterruptedException, ApiException {
        String data = gson.toJson(record);
        HttpRequest request = apiRequestCreator.createRecordUploadRequest(account, data);

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        return switch (statusCode) {
            case 200 -> {
                UploadSuccessJson success = gson.fromJson(response.body(), UploadSuccessJson.class);
                yield success.update();
            }
            case 400, 404, 500 -> {
                UploadFailureJson failure = gson.fromJson(response.body(), UploadFailureJson.class);
                throw new ApiException(failure.message());
            }
            default -> throw new ApiException(statusCode + " Network Error");
        };
    }
}
