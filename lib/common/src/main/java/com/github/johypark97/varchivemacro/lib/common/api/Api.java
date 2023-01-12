package com.github.johypark97.varchivemacro.lib.common.api;

import com.github.johypark97.varchivemacro.lib.common.api.impl.RecordFetcherImpl;
import com.github.johypark97.varchivemacro.lib.common.api.impl.RecordUploaderImpl;
import com.github.johypark97.varchivemacro.lib.common.api.impl.StaticFetcherImpl;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.net.ssl.SSLContext;

public final class Api {
    private static HttpClient httpClient;

    public static HttpClient newHttpClient() throws GeneralSecurityException {
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new GeneralSecurityException(e);
        }

        return HttpClient.newBuilder().sslContext(sslContext).version(Version.HTTP_2).build();
    }

    public static synchronized HttpClient getHttpClient() throws GeneralSecurityException {
        if (httpClient == null) {
            httpClient = newHttpClient();
        }

        return httpClient;
    }

    public static RecordFetcher newRecordFetcher(String djName) throws GeneralSecurityException {
        return new RecordFetcherImpl(getHttpClient(), djName);
    }

    public static RecordUploader newRecordUploader(int userNo, UUID token)
            throws GeneralSecurityException {
        return new RecordUploaderImpl(getHttpClient(), userNo, token);
    }

    public static StaticFetcher newStaticFetcher() throws GeneralSecurityException {
        return new StaticFetcherImpl(getHttpClient());
    }
}
