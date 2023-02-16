package com.github.johypark97.varchivemacro.macro.gui.presenter;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;

public interface IScannerTask {
    interface Presenter {
        void start(JFrame parent, ScannerTaskViewData data);

        void viewClosed();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void setData(ScannerTaskViewData viewData);
    }


    class ScannerTaskViewData {
        public Image fullImage;
        public Image titleImage;
        public final Map<String, RecordData> records = new HashMap<>();

        public void addRecord(String key, Image rateImage, Image maxComboImage, String rate,
                boolean maxCombo) {
            RecordData data = new RecordData();
            data.maxCombo = maxCombo;
            data.maxComboImage = maxComboImage;
            data.rate = rate;
            data.rateImage = rateImage;

            records.put(key, data);
        }

        public static class RecordData {
            public Image rateImage;
            public Image maxComboImage;
            public String rate;
            public boolean maxCombo;
        }
    }
}
