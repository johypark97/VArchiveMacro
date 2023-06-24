package com.github.johypark97.varchivemacro.lib.common.api;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Board;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;

public interface RecordFetcher {
    SuccessJson getResult();

    void fetch(Button button, Board board) throws IOException, InterruptedException, ApiException;

    class SuccessJson {
        @Expose
        public boolean success;

        @Expose
        public Board board;

        @Expose
        public Button button;

        @Expose
        public int totalCount;

        @Expose
        public List<FloorJson> floors;

        public static SuccessJson fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, SuccessJson.class);
        }
    }


    class FloorJson {
        @Expose
        public float floorNumber;

        @Expose
        public List<PatternJson> patterns;
    }


    class PatternJson {
        @Expose
        @SerializedName("title")
        public int id;

        @Expose
        @SerializedName("name")
        public String title;

        @Expose
        public String composer;

        @Expose
        public Pattern pattern;

        @Expose
        @SerializedName("score")
        public float rate;

        @Expose
        public int maxCombo;

        @Expose
        public String dlc;

        @Expose
        public String dlcCode;
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
