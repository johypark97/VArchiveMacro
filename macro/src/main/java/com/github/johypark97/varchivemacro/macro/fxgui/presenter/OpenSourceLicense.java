package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import javafx.collections.ObservableList;

public interface OpenSourceLicense {
    interface OpenSourceLicensePresenter
            extends CommonPresenter<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void showLicense(String library);
    }


    interface OpenSourceLicenseView
            extends CommonView<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void startView();

        void setLibraryList(ObservableList<String> list);

        void setLicenseText(String text);

        void setLicenseUrl(String url);
    }
}
