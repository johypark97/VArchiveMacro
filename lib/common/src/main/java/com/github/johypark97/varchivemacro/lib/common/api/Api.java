package com.github.johypark97.varchivemacro.lib.common.api;

import com.github.johypark97.varchivemacro.lib.common.api.impl.RecordFetcherImpl;
import com.github.johypark97.varchivemacro.lib.common.api.impl.RecordUploaderImpl;
import com.github.johypark97.varchivemacro.lib.common.api.impl.StaticFetcherImpl;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.net.ssl.SSLContext;

public final class Api {
    public enum Board {
        _1("1"), _2("2"), _3("3"), _4("4"), _5("5"), _6("6"), _7("7"), _8("8"), _9("9"), _10(
                "10"), _11("11"), MX("MX"), SC("SC"), SC5("SC5"), SC10("SC10"), SC15("SC15");

        private final String value;

        Board(String s) {
            value = s;
        }

        @Override
        public String toString() {
            return value;
        }
    }


    public enum Button {
        _4(0, 4), _5(1, 5), _6(2, 6), _8(3, 8);

        private final int value;
        private final int weight;

        Button(int w, int i) {
            value = i;
            weight = w;
        }

        public int getValue() {
            return value;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

        public static class GsonSerializer implements JsonSerializer<Button> {
            @Override
            public JsonElement serialize(Button src, Type typeOfSrc,
                    JsonSerializationContext context) {
                return new JsonPrimitive(src.getValue());
            }
        }
    }


    public enum Pattern {
        NM(0, "NORMAL", "NM"), HD(1, "HARD", "HD"), MX(2, "MAXIMUM", "MX"), SC(3, "SC", "SC");

        private final String fullName;
        private final String shortName;
        private final int weight;

        Pattern(int w, String f, String s) {
            fullName = f;
            shortName = s;
            weight = w;
        }

        public String getShortName() {
            return shortName;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }


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
