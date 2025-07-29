package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import java.util.List;

public interface OpenSourceLicense {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void showLicense(String value);

        void openWebBrowser(String url);
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setLibraryList(List<String> value);

        void setLicenseText(String value);

        void setCopyrightOwner(String value);

        void setLibraryUrl(String value);
    }
}
