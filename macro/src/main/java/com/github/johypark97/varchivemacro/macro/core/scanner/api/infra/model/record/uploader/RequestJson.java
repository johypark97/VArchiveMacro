package com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.model.record.uploader;

import static com.github.johypark97.varchivemacro.macro.common.GsonWrapper.newGsonBuilder_general;

import com.github.johypark97.varchivemacro.macro.libscanner.Enums;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestJson {
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

    public RequestJson(String title,
                       Enums.Button button,
                       Enums.Pattern pattern,
                       float rate,
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
        return "RequestData{" + "title='" + title + '\'' + ", dlc='" + dlc + '\'' + ", composer='"
                + composer + '\'' + ", button=" + button + ", pattern='" + pattern + '\''
                + ", rate=" + rate + ", maxCombo=" + maxCombo + '}';
    }
}
