package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class CollectionTaskData {
    public Image fullImage;
    public Image titleImage;
    public final Map<String, RecordData> records = new HashMap<>();

    public void addRecord(String key, RecordData data) {
        records.put(key, data);
    }

    public static class RecordData {
        public Image rateImage;
        public Image maxComboImage;
        public String rate;
        public boolean maxCombo;
    }
}
