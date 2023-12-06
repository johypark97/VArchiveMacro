package com.github.johypark97.varchivemacro.lib.common.database;

import com.github.johypark97.varchivemacro.lib.common.Enums.Button;
import com.github.johypark97.varchivemacro.lib.common.Enums.Pattern;
import java.util.Map;

public interface RecordManager {
    boolean updateRecord(LocalRecord record);

    LocalRecord findSameRecord(LocalRecord record);

    LocalRecord getRecord(int id, Button button, Pattern pattern);

    Map<Pattern, LocalRecord> getRecord(int id, Button button);

    Map<Button, Map<Pattern, LocalRecord>> getRecord(int id);

    class LocalRecord {
        public final Button button;
        public final Pattern pattern;
        public final int id;

        public boolean maxCombo;
        public float rate;

        public LocalRecord(int id, Button button, Pattern pattern, float rate, boolean maxCombo) {
            if (rate < 0.0f || rate > 100.0f) {
                throw new IllegalArgumentException("invalid rate: " + rate);
            }

            this.button = button;
            this.id = id;
            this.maxCombo = maxCombo || rate == 100.0f;
            this.pattern = pattern;
            this.rate = rate;
        }

        public boolean isUpdated(LocalRecord record) {
            if (id != record.id || button != record.button || pattern != record.pattern) {
                String format = "%d. %sB %s";
                String a = String.format(format, id, button, pattern);
                String b = String.format(format, record.id, record.button, record.pattern);
                String message = String.format("is a different song: %s <-> %s", a, b);
                throw new IllegalArgumentException(message);
            }

            return rate < record.rate || (!maxCombo && record.maxCombo);
        }

        public boolean update(LocalRecord record) {
            if (!isUpdated(record)) {
                return false;
            }

            rate = Math.max(rate, record.rate);
            maxCombo = maxCombo | record.maxCombo;
            return true;
        }
    }
}
