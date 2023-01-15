package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

import com.github.johypark97.varchivemacro.lib.common.api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Pattern;
import com.google.gson.annotations.Expose;

public class LocalRecord {
    @Expose
    public final Button button;

    @Expose
    public final Pattern pattern;

    @Expose
    public float score;

    @Expose
    public int maxCombo;

    public LocalRecord(Button button, Pattern pattern, float score, int maxCombo) {
        if (score < 0.0f || score > 100.0f) {
            throw new IllegalArgumentException("invalid score: " + score);
        }

        this.button = button;
        this.maxCombo = (score == 100.0f || maxCombo != 0) ? 1 : 0;
        this.pattern = pattern;
        this.score = score;
    }

    // Temporary method to resolve spotbugs URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD warning.
    @Override
    public String toString() {
        return String.format("%sB %s %.2f(%d)", button, pattern, score, maxCombo);
    }
}
