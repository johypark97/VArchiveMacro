package com.github.johypark97.varchivemacro.lib.common.database.datastruct;

import com.google.gson.annotations.Expose;
import java.util.List;

public class Record {
    private static final List<Integer> BUTTONS = List.of(4, 5, 6, 8);
    private static final List<String> PATTERNS = List.of("NM", "HD", "MX", "SC");

    @Expose
    public final int button;

    @Expose
    public final String pattern;

    @Expose
    public float score;

    @Expose
    public int maxCombo;

    public Record(int button, String pattern, float score, int maxCombo) {
        if (!BUTTONS.contains(button)) {
            throw new IllegalArgumentException("unknown button: " + button);
        }

        if (!PATTERNS.contains(pattern)) {
            throw new IllegalArgumentException("invalid pattern: " + pattern);
        }

        if (score < 0.0f || score > 100.0f) {
            throw new IllegalArgumentException("invalid score: " + score);
        }

        this.button = button;
        this.maxCombo = (score == 100.0f || maxCombo != 0) ? 1 : 0;
        this.pattern = pattern;
        this.score = score;
    }

    @Override
    public String toString() {
        return String.format("%sB %s %.2f(%d)", button, pattern, score, maxCombo);
    }
}
