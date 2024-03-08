package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void onViewShow();

        void scanner_setup_onLoadRemoteRecord(String djName);
    }


    interface HomeView extends MvpView<HomePresenter> {
        void showError(String message, Exception e);

        ScannerSetupView getScannerSetupView();

        void scanner_setup_loadRemoteRecord(String djName);

        interface ScannerSetupView {
            void showForbiddenMark();

            void showLoadingMark(String djName);

            void hideLoadingMark();

            void showDjNameInput();

            void hideDjNameInput();

            void showDjNameInputError(String message);

            void hideDjNameInputError();

            void showScanner();
        }
    }
}
