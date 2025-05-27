package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import java.util.List;

public interface OpenSourceLicense {
    interface OpenSourceLicensePresenter
            extends Mvp.MvpPresenter<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void startView();

        void showLicense(String value);

        void openWebBrowser(String url);
    }


    interface OpenSourceLicenseView
            extends Mvp.MvpView<OpenSourceLicenseView, OpenSourceLicensePresenter> {
        void showLibraryList(List<String> value);

        void showLicenseText(String value);

        void showLibraryUrl(String value);
    }
}
