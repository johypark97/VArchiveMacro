package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.core.Button;
import com.github.johypark97.varchivemacro.macro.core.Pattern;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.awt.Image;
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
        public final Table<Button, Pattern, RecordData> records = HashBasedTable.create();

        public void addRecord(Button button, Pattern pattern, Image rateImage, Image maxComboImage,
                String rate, boolean maxCombo) {
            RecordData data = new RecordData();
            data.maxCombo = maxCombo;
            data.maxComboImage = maxComboImage;
            data.rate = rate;
            data.rateImage = rateImage;

            records.put(button, pattern, data);
        }

        public static class RecordData {
            public Image rateImage;
            public Image maxComboImage;
            public String rate;
            public boolean maxCombo;
        }
    }
}
