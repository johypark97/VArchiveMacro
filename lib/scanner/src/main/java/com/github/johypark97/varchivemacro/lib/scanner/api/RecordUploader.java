package com.github.johypark97.varchivemacro.lib.scanner.api;

import static com.github.johypark97.varchivemacro.lib.common.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;

public interface RecordUploader {
    boolean getResult();

    void upload(RequestJson data) throws IOException, InterruptedException, ApiException;

    class SuccessJson {
        @Expose
        public boolean success;

        @Expose
        public boolean update;

        public static SuccessJson fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, SuccessJson.class);
        }
    }


    final class RequestJson {
        @Expose
        @SerializedName("name")
        public String title;

        @Expose
        public String dlc;

        @Expose
        public String composer;

        @Expose
        public int button;

        @Expose
        public String pattern;

        @Expose
        @SerializedName("score")
        public float rate;

        @Expose
        public int maxCombo;

        public RequestJson(String title, Button button, Pattern pattern, float rate,
                boolean maxCombo) {
            if (rate < 0.0f || rate > 100.0f) {
                throw new IllegalArgumentException("invalid rate: " + rate);
            }

            this.title = title;
            this.button = button.toInt();
            this.pattern = pattern.toString();
            this.rate = rate;
            this.maxCombo = (maxCombo || rate == 100.0f) ? 1 : 0;
        }

        public String toJson() {
            Gson gson = newGsonBuilder_general().create();
            return gson.toJson(this);
        }

        // Temporary method to resolve spotbugs URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD warning.
        @Override
        public String toString() {
            return "RequestData{" + "title='" + title + '\'' + ", dlc='" + dlc + '\''
                    + ", composer='" + composer + '\'' + ", button=" + button + ", pattern='"
                    + pattern + '\'' + ", rate=" + rate + ", maxCombo=" + maxCombo + '}';
        }
    }


    class FailureJson {
        @Expose
        public String message;

        public static FailureJson fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, FailureJson.class);
        }
    }
}
