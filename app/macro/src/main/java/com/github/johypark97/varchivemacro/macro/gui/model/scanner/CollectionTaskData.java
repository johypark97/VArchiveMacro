package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CollectionTaskData {
    public Image fullImage;
    public Image titleImage;
    public final Map<String, RecordData> records = new HashMap<>();

    public void setFullImage(byte[] pngBytes) throws IOException {
        fullImage = ImageConverter.pngBytesToImage(pngBytes);
    }

    public void setTitleImage(byte[] pngBytes) throws IOException {
        titleImage = ImageConverter.pngBytesToImage(pngBytes);
    }

    public void addRecord(String key, Image rateImage, String rate) {
        RecordData data = new RecordData();
        data.rateImage = rateImage;
        data.rate = rate;
        records.put(key, data);
    }

    public static class RecordData {
        public Image rateImage;
        public Image maxComboImage;
        public String rate;
        public boolean maxCombo;
    }
}
