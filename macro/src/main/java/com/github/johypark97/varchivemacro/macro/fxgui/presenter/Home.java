package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpView;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.Window;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void onViewShow_setupService();

        void onViewShow_setupCacheDirectory(TextField textField);

        void onViewShow_setupCaptureDelayLinker(SliderTextFieldLinker linker);

        void onViewShow_setupKeyInputDurationLinker(SliderTextFieldLinker linker);

        void onViewShow_setupAccountFile(TextField textField);

        void onViewShow_setupRecordUploadDelayLinker(SliderTextFieldLinker linker);

        boolean onViewShow_loadDatabase();

        void onViewShow_loadRecord();

        void home_onChangeLanguage(Locale locale);

        void scanner_front_onLoadRemoteRecord(String djName);

        void scanner_viewer_onShowSongTree(TreeView<ViewerTreeData> treeView, String filter);

        ViewerRecordData scanner_viewer_onShowRecord(int id);

        void scanner_capture_onOpenCaptureViewer(Window ownerWindow, String cacheDirectory, int id);

        void scanner_capture_onClearScanData();

        void scanner_capture_onStart(Set<String> selectedTabSet, String cacheDirectory,
                int captureDelay, int keyInputDuration);

        void scanner_capture_onStop();

        void scanner_song_onOpenLinkEditor(Window ownerWindow, String cacheDirectory, int id);

        Path scanner_option_onOpenCacheDirectorySelector(Window ownerWindow);

        Path scanner_option_onOpenAccountFileSelector(Window ownerWindow);
    }


    interface HomeView extends MvpView<HomePresenter> {
        void showError(String header, Throwable throwable);

        void showInformation(String header, String message);

        void home_changeLanguage(Locale locale);

        ScannerFrontController getScannerFrontController();

        void scanner_front_loadRemoteRecord(String djName);

        void scanner_viewer_showSongTree(String filter);

        void scanner_viewer_showRecord(int id);

        void scanner_capture_setCaptureDataList(ObservableList<CaptureData> list);

        void scanner_capture_openCaptureViewer(int id);

        void scanner_capture_clearScanData();

        void scanner_capture_refresh();

        void scanner_capture_setTabList(List<String> list);

        Set<String> scanner_capture_getSelectedTabSet();

        void scanner_capture_setSelectedTabSet(Set<String> value);

        void scanner_capture_start();

        void scanner_capture_stop();

        void scanner_song_setSongDataList(ObservableList<SongData> list);

        void scanner_song_openLinkEditor(int id);

        void scanner_song_refresh();

        String scanner_option_getCacheDirectory();

        int scanner_option_getCaptureDelay();

        int scanner_option_getKeyInputDuration();

        String scanner_option_getAccountFile();

        int scanner_option_getRecordUploadDelay();

        void scanner_option_openCacheDirectorySelector();

        void scanner_option_openAccountFileSelector();
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
