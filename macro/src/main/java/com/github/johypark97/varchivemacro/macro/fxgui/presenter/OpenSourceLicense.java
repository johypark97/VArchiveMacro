package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import javafx.collections.ObservableList;
import javafx.stage.Window;

public interface OpenSourceLicense {
    interface OpenSourceLicensePresenter extends MvpPresenter<OpenSourceLicenseView> {
        StartData getStartData();

        void setStartData(StartData value);

        void onViewShown();

        String onShowLicenseText(String library);

        String onShowLibraryUrl(String library);
    }


    interface OpenSourceLicenseView extends MvpView<OpenSourceLicensePresenter> {
        void setLibraryList(ObservableList<String> list);

        void showLicenseText(String library);

        void showLibraryUrl(String library);
    }


    class StartData {
        public Window ownerWindow;
    }
}
