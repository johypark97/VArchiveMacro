package com.github.johypark97.varchivemacro.dbmanager.gui.model;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;

public interface OcrTesterModel {
    int getCount();

    void clear();

    void addData(OcrTesterData data);

    OcrTesterData getData(int index);

    interface OcrTesterData {
        LocalSong getTestSong();

        String getTestSong_normalizedTitle();

        LocalSong getRecognizedSong();

        void setRecognizedSong(LocalSong value);

        String getScannedNormalizedTitle();

        void setScannedNormalizedTitle(String value);

        float getAccuracy();

        void setAccuracy(float value);

        int getDistance();

        void setDistance(int value);

        String getFound();

        void setFound(String value);

        String getNote();

        void setNote(String value);
    }
}
