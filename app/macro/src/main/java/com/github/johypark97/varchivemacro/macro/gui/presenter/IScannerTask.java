package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.gui.model.ScannerTaskModels.ResponseData;
import javax.swing.JFrame;

public interface IScannerTask {
    interface Presenter {
        void start(JFrame parent, ResponseData data);

        void viewClosed();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void setData(ResponseData data);
    }
}
