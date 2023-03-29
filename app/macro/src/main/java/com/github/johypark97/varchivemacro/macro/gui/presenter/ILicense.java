package com.github.johypark97.varchivemacro.macro.gui.presenter;

import java.util.List;
import javax.swing.JFrame;

public interface ILicense {
    interface Presenter {
        void start(JFrame parent);

        void viewClosed();

        void getLicense(String key);
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void setLicenseList(List<String> licenses);

        void setLicenseText(String text);

        void setLicenseUrl(String url);
    }
}
