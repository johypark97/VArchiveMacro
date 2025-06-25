package com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model;

import java.util.Objects;

public record SongRecord(float rate, boolean maxCombo) implements Comparable<SongRecord> {
    public SongRecord {
        if (rate < 0 || rate > 100) {
            throw new IllegalArgumentException("Rate values must be between 0 and 100.");
        }
    }

    @Override
    public int compareTo(SongRecord o) {
        Objects.requireNonNull(o);

        if (rate < o.rate) {
            return -1;
        } else if (rate > o.rate) {
            return 1;
        }

        if (maxCombo && !o.maxCombo) {
            return 1;
        } else if (!maxCombo && o.maxCombo) {
            return -1;
        }

        return 0;
    }
}
