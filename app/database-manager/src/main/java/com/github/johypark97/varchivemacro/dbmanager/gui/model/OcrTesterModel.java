package com.github.johypark97.varchivemacro.dbmanager.gui.model;

public interface OcrTesterModel {
    int getCount();

    void clear();

    void addData(OcrTesterData data);

    OcrTesterData getData(int index);

    interface OcrTesterData {
        String getComposer();

        String getDlc();

        String getDlcTab();

        String getNormalizedTitle();

        void setNormalizedTitle(String value);

        String getScannedTitle();

        void setScannedTitle(String value);

        String getTitle();

        float getAccuracy();

        void setAccuracy(float value);

        int getDistance();

        void setDistance(int value);

        int getId();
    }
}
