package com.github.johypark97.varchivemacro.lib.common.api;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;

public interface RecordUploader {
    boolean getResult();

    void upload(RequestJson data) throws IOException, InterruptedException;

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


    class RequestJson {
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
        public float score;

        @Expose
        public int maxCombo;

        public RequestJson(String title, Button button, Pattern pattern, float score,
                int maxCombo) {
            if (score < 0.0f || score > 100.0f) {
                throw new IllegalArgumentException("invalid score: " + score);
            }

            this.title = title;
            this.button = button.getValue();
            this.pattern = pattern.toString();
            this.score = score;
            this.maxCombo = (score == 100.0f || maxCombo != 0) ? 1 : 0;
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
                    + pattern + '\'' + ", score=" + score + ", maxCombo=" + maxCombo + '}';
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
