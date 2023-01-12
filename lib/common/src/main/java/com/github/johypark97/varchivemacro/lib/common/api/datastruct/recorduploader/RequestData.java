package com.github.johypark97.varchivemacro.lib.common.api.datastruct.recorduploader;

import com.github.johypark97.varchivemacro.lib.common.api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Pattern;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestData {
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

    public RequestData(String title, Button button, Pattern pattern, float score, int maxCombo) {
        if (score < 0.0f || score > 100.0f) {
            throw new IllegalArgumentException("invalid score: " + score);
        }

        this.title = title;
        this.button = button.getValue();
        this.pattern = pattern.toString();
        this.score = score;
        this.maxCombo = (score == 100.0f || maxCombo != 0) ? 1 : 0;
    }

    // Temporary method to resolve spotbugs URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD warning.
    @Override
    public String toString() {
        return "RequestData{" + "title='" + title + '\'' + ", dlc='" + dlc + '\'' + ", composer='"
                + composer + '\'' + ", button=" + button + ", pattern='" + pattern + '\''
                + ", score=" + score + ", maxCombo=" + maxCombo + '}';
    }
}
