package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
        private final String composer;
        private final String dlc;
        private final String dlcTab;
        private final String title;
        private final int id;

        private String normalizedTitle;
        private String scannedTitle;
        private float accuracy;
        private int distance;

        public DefaultOcrTesterData(LocalSong song) {
            composer = song.composer();
            dlc = song.dlc();
            dlcTab = song.dlcTab();
            id = song.id();
            title = song.title();
        }

        @Override
        public String getComposer() {
            return composer;
        }

        @Override
        public String getDlc() {
            return dlc;
        }

        @Override
        public String getDlcTab() {
            return dlcTab;
        }

        @Override
        public String getNormalizedTitle() {
            return normalizedTitle;
        }

        @Override
        public void setNormalizedTitle(String value) {
            normalizedTitle = value;
        }

        @Override
        public String getScannedTitle() {
            return scannedTitle;
        }

        @Override
        public void setScannedTitle(String value) {
            scannedTitle = value;
        }

        @Override
        public String getTitle() {
            return title;
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
        public int getId() {
            return id;
        }
    }
}
