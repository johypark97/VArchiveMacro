package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.awt.Image;

public class CollectionTaskData {
    public Exception exception;
    public Image fullImage;
    public Image titleImage;
    public final Table<Button, Pattern, RecordData> records = HashBasedTable.create();

    public void addRecord(Button button, Pattern pattern, RecordData data) {
        records.put(button, pattern, data);
    }

    public static class RecordData {
        public Image rateImage;
        public Image maxComboImage;
        public String rate;
        public boolean maxCombo;
    }
}
