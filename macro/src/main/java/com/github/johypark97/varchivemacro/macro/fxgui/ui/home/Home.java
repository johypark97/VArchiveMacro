package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public interface Home {
    interface HomePresenter extends CommonPresenter<HomeView, HomePresenter> {
        void home_changeLanguage(Locale locale);

        void home_openOpenSourceLicense();

        void home_openAbout();

        void scanner_front_loadRemoteRecord(String djName);

        void scanner_viewer_updateSongTreeViewFilter(String filter);

        void scanner_viewer_showRecord(int id);

        void scanner_capture_openCaptureViewer(int id);

        void scanner_capture_clearScanData();

        void scanner_capture_start();

        void scanner_capture_stop();

        void scanner_song_openLinkEditor(int id);

        void scanner_analysis_clearAnalysisData();

        void scanner_analysis_openAnalysisDataViewer(int id);

        void scanner_analysis_startAnalysis();

        void scanner_analysis_stopAnalysis();

        void scanner_uploader_refresh();

        void scanner_uploader_startUpload(long count);

        void scanner_uploader_stopUpload();

        void scanner_option_openCacheDirectorySelector();

        void scanner_option_openAccountFileSelector();

        void macro_start_up();

        void macro_start_down();

        void macro_stop();
    }


    interface HomeView extends CommonView<HomeView, HomePresenter> {
        void startView();

        void showError(String header, Throwable throwable);

        void showInformation(String header, String content);

        boolean showConfirmation(String header, String content);

        void home_openAbout();

        ScannerFrontController getScannerFrontController();

        void scanner_viewer_setSongTreeViewRoot(TreeItem<ViewerTreeData> root);

        void scanner_viewer_setSongInformationText(String value);

        void scanner_viewer_setRecordData(ViewerRecordData data);

        void scanner_capture_setCaptureDataList(ObservableList<CaptureData> list);

        void scanner_capture_refresh();

        void scanner_capture_setTabList(List<String> list);

        Set<String> scanner_capture_getSelectedCategorySet();

        void scanner_capture_setSelectedCategorySet(Set<String> value);

        void scanner_song_setSongDataList(ObservableList<SongData> list);

        void scanner_song_refresh();

        void scanner_analysis_setAnalysisDataList(ObservableList<AnalysisData> list);

        void scanner_analysis_setProgressBarValue(double value);

        void scanner_analysis_setProgressLabelText(String value);

        void scanner_uploader_setNewRecordDataList(ObservableList<NewRecordData> list);

        String scanner_option_getCacheDirectory();

        void scanner_option_setCacheDirectory(String value);

        void scanner_option_setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int scanner_option_getCaptureDelay();

        void scanner_option_setupKeyInputDurationSlider(int defaultValue, int limitMax,
                int limitMin, int value);

        int scanner_option_getKeyInputDuration();

        void scanner_option_setupAnalysisThreadCountSlider(int defaultValue, int max, int value);

        int scanner_option_getupAnalysisThreadCount();

        String scanner_option_getAccountFile();

        void scanner_option_setAccountFile(String value);

        void scanner_option_setupRecordUploadDelaySlider(int defaultValue, int limitMax,
                int limitMin, int value);

        int scanner_option_getRecordUploadDelay();

        File scanner_option_openCacheDirectorySelector(Path initialDirectory);

        File scanner_option_openAccountFileSelector(Path initialDirectory);

        AnalysisKey macro_getAnalysisKey();

        void macro_setAnalysisKey(AnalysisKey key);

        void macro_setupCountSlider(int defaultValue, int limitMax, int limitMin, int value);

        int macro_getCount();

        void macro_setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin, int value);

        int macro_getCaptureDelay();

        void macro_setupCaptureDurationSlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int macro_getCaptureDuration();

        void macro_setupKeyInputDurationSlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int macro_getKeyInputDuration();
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
        public final Song song;
        public final String name;

        public ViewerTreeData(String name) {
            this.name = name;

            song = null;
        }

        public ViewerTreeData(Song song) {
            this.song = song;

            name = null;
        }
    }


    class ViewerRecordData {
        public final boolean[][] maxCombo = new boolean[4][4];
        public final float[][] rate = new float[4][4];

        public ViewerRecordData() {
            for (int i = 0; i < 4; ++i) {
                for (int j = 0; j < 4; ++j) {
                    rate[i][j] = -1;
                }
            }
        }
    }
}
