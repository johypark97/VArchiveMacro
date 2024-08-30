package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import javafx.collections.ObservableList;

public interface OpenSourceLicense {
    interface OpenSourceLicensePresenter
            extends MvpPresenter<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void onStartView();

        void showLicense(String library);
    }


    interface OpenSourceLicenseView
            extends MvpView<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void setLibraryList(ObservableList<String> list);

        void setLicenseText(String text);

        void setLicenseUrl(String url);
    }
}
