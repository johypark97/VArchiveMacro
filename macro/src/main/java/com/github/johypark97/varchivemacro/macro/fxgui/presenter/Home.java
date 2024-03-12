package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView.ViewerTreeData;
import javafx.scene.control.TreeView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void onViewShow();

        void scanner_setup_onLoadRemoteRecord(String djName);

        void scanner_viewer_onShowSongTree(TreeView<ViewerTreeData> treeView);
    }


    interface HomeView extends MvpView<HomePresenter> {
        void showError(String message, Exception e);

        ScannerSetupView getScannerSetupView();

        void scanner_setup_loadRemoteRecord(String djName);

        void scanner_viewer_showSongTree();

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


        class ViewerTreeData {
            public final LocalDlcSong song;
            public final String name;

            public ViewerTreeData(String name) {
                this.name = name;

                song = null;
            }

            public ViewerTreeData(LocalDlcSong song) {
                this.song = song;

                name = null;
            }
        }
    }
}
