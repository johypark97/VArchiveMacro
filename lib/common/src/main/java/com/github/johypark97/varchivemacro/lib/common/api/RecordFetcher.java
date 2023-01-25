package com.github.johypark97.varchivemacro.lib.common.api;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_general;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import java.util.List;

public interface RecordFetcher {
    Success getResult();

    void fetch(Button button, Board board) throws IOException, InterruptedException;

    class Success {
        @Expose
        public boolean success; // NOPMD

        @Expose
        public String board;

        @Expose
        public int button;

        @Expose
        public int totalCount;

        @Expose
        public List<Floor> floors;

        public static Success fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, Success.class);
        }
    }


    class Floor {
        @Expose
        public float floorNumber;

        @Expose
        public List<Pattern> patterns;
    }


    class Pattern {
        @Expose
        @SerializedName("title")
        public int id;

        @Expose
        @SerializedName("name")
        public String title;

        @Expose
        public String composer;

        @Expose
        public String pattern; // NOPMD

        @Expose
        public float score;

        @Expose
        public int maxCombo;

        @Expose
        public String dlc;

        @Expose
        public String dlcCode;
    }


    class Failure {
        @Expose
        public String message;

        public static Failure fromJson(String json) {
            Gson gson = newGsonBuilder_general().create();
            return gson.fromJson(json, Failure.class);
        }
    }
}
