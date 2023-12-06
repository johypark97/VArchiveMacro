package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class DefaultOcrTesterModel implements OcrTesterModel {
    private final List<OcrTesterData> dataList = new CopyOnWriteArrayList<>();

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public void clear() {
        dataList.clear();
    }

    @Override
    public void addData(OcrTesterData data) {
        dataList.add(data);
    }

    @Override
    public OcrTesterData getData(int index) {
        return dataList.get(index);
    }

    public static class DefaultOcrTesterData implements OcrTesterData {
        private final LocalDlcSong testSong;
        private final String testSong_normalizedTitle;

        private LocalDlcSong recognizedSong;
        private String found = "";
        private String note = "";
        private String scannedNormalizedTitle = "";
        private float accuracy;
        private int distance;

        public DefaultOcrTesterData(LocalDlcSong testSong, TitleTool titleTool,
                Function<String, String> normalizer) {
            this.testSong = testSong;

            testSong_normalizedTitle = normalizer.apply(titleTool.getShortTitle(testSong));
        }

        @Override
        public LocalDlcSong getTestSong() {
            return testSong;
        }

        @Override
        public String getTestSong_normalizedTitle() {
            return testSong_normalizedTitle;
        }

        @Override
        public LocalDlcSong getRecognizedSong() {
            return recognizedSong;
        }

        @Override
        public void setRecognizedSong(LocalDlcSong value) {
            recognizedSong = value;
        }

        @Override
        public String getScannedNormalizedTitle() {
            return scannedNormalizedTitle;
        }

        @Override
        public void setScannedNormalizedTitle(String value) {
            scannedNormalizedTitle = value;
        }

        @Override
        public float getAccuracy() {
            return accuracy;
        }

        @Override
        public void setAccuracy(float value) {
            accuracy = value;
        }

        @Override
        public int getDistance() {
            return distance;
        }

        @Override
        public void setDistance(int value) {
            distance = value;
        }

        @Override
        public String getFound() {
            return found;
        }

        @Override
        public void setFound(String value) {
            found = value;
        }

        @Override
        public String getNote() {
            return note;
        }

        @Override
        public void setNote(String value) {
            note = value;
        }
    }
}
