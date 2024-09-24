package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;

public interface ScannerLoader {
    interface ScannerLoaderPresenter
            extends CommonPresenter<ScannerLoaderView, ScannerLoaderPresenter> {
        void loadRemoteRecord();
    }


    interface ScannerLoaderView extends MvpView<ScannerLoaderView, ScannerLoaderPresenter> {
        void startView();

        String getDjNameText();

        void showForbiddenMark();

        void showLoadingMark(String djName);

        void hideLoadingMark();

        void showDjNameInput();

        void showDjNameInputError(String message);

        void hideDjNameInputError();
    }
}
