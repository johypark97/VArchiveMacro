package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scanner;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public interface Scanner {
    interface ScannerPresenter extends CommonPresenter<ScannerView, ScannerPresenter> {
        void viewer_updateSongTreeViewFilter(String filter);

        void viewer_showRecord(int id);

        void capture_openCaptureViewer(int id);

        void capture_clearScanData();

        void capture_start();

        void capture_stop();

        void song_openLinkEditor(int id);

        void analysis_clearAnalysisData();

        void analysis_openAnalysisDataViewer(int id);

        void analysis_startAnalysis();

        void analysis_stopAnalysis();

        void uploader_refresh();

        void uploader_startUpload(long count);

        void uploader_stopUpload();

        void option_openCacheDirectorySelector();

        void option_openAccountFileSelector();
    }


    interface ScannerView extends MvpView<ScannerView, ScannerPresenter> {
        void startView();

        void stopView();

        void viewer_setSongTreeViewRoot(TreeItem<ViewerTreeData> root);

        void viewer_setSongInformationText(String value);

        void viewer_setRecordData(ViewerRecordData data);

        void capture_setCaptureDataList(ObservableList<ScanDataManager.CaptureData> list);

        void capture_refresh();

        void capture_setTabList(List<String> list);

        Set<String> capture_getSelectedCategorySet();

        void capture_setSelectedCategorySet(Set<String> value);

        void song_setSongDataList(ObservableList<ScanDataManager.SongData> list);

        void song_refresh();

        void analysis_setAnalysisDataList(ObservableList<AnalysisDataManager.AnalysisData> list);

        void analysis_setProgressBarValue(double value);

        void analysis_setProgressLabelText(String value);

        void uploader_setNewRecordDataList(ObservableList<NewRecordDataManager.NewRecordData> list);

        String option_getCacheDirectory();

        void option_setCacheDirectory(String value);

        void option_setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int option_getCaptureDelay();

        void option_setupKeyInputDurationSlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int option_getKeyInputDuration();

        void option_setupAnalysisThreadCountSlider(int defaultValue, int max, int value);

        int option_getAnalysisThreadCount();

        String option_getAccountFile();

        void option_setAccountFile(String value);

        void option_setupRecordUploadDelaySlider(int defaultValue, int limitMax, int limitMin,
                int value);

        int option_getRecordUploadDelay();
    }


    class ViewerTreeData {
        public final SongDatabase.Song song;
        public final String name;

        public ViewerTreeData(String name) {
            this.name = name;

            song = null;
        }

        public ViewerTreeData(SongDatabase.Song song) {
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
