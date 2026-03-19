package com.github.johypark97.varchivemacro.lib.scanner.api;

import static com.github.johypark97.varchivemacro.libcommon.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;

public interface RecordFetcher {
    SuccessJson getResult();

    void fetch(Button button) throws IOException, InterruptedException, ApiException;

    class SuccessJson {
        @Expose
        public boolean success;

        @Expose
        @SerializedName("nickname")
        public String djName;

        @Expose
        public Button button;

        @Expose
        public int count;

        @Expose
        public List<RecordJson> records;

        public static SuccessJson fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, SuccessJson.class);
        }
    }

    class RecordJson {
        @Expose
        @SerializedName("title")
        public int id;

        @Expose
        public String name;

        @Expose
        public String dlcCode;

        @Expose
        public Pattern pattern;

        @Expose
        public int level;

        /**
         * nullable
         */
        @Expose
        public Integer floor;

        /**
         * nullable
         */
        @Expose
        public String floorName;

        @Expose
        public boolean newTab;

        @Expose
        public int maxRating;

        @Expose
        public float score;

        @Expose
        public boolean maxCombo;

        /**
         * nullable
         */
        @Expose
        public Double rating;

        @Expose
        public double djpower;

        /**
         * nullable
         */
        @Expose
        public Double maxDjpower;

        /**
         * nullable
         */
        @Expose
        public String updatedAt;
    }

    class FailureJson {
        /**
         * nullable
         */
        @Expose
        public Boolean success;

        /**
         * nullable
         */
        @Expose
        public Integer errorCode;

        @Expose
        public String message;

        public static FailureJson fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, FailureJson.class);
        }
    }
}
