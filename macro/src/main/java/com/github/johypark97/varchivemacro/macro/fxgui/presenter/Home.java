package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import java.util.List;
import java.util.Set;
import javafx.scene.control.TreeView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        boolean onViewShow_loadDatabase();

        void onViewShow_loadRecord();

        void scanner_front_onLoadRemoteRecord(String djName);

        void scanner_viewer_onShowSongTree(TreeView<ViewerTreeData> treeView, String filter);

        ViewerRecordData scanner_viewer_onShowRecord(int id);
    }


    interface HomeView extends MvpView<HomePresenter> {
        void showError(String message, Exception e);

        ScannerFrontController getScannerFrontController();

        void scanner_front_loadRemoteRecord(String djName);

        void scanner_viewer_showSongTree(String filter);

        void scanner_viewer_showRecord(int id);

        void scanner_scanner_setTabList(List<String> list);

        Set<String> scanner_scanner_getSelectedTabSet();

        void scanner_scanner_setSelectedTabSet(Set<String> value);
    }


    interface ScannerFrontController {
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


    class ViewerRecordData {
        public final boolean[][] maxCombo = new boolean[4][4];
        public final float[][] rate = new float[4][4];

        public String composer;
        public String title;

        public ViewerRecordData() {
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    rate[i][j] = -1;
                }
            }
        }
    }
}
