package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.lib.common.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;
import java.nio.file.Path;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        boolean initialize();

        boolean terminate();

        void onViewShowing_viewer_linkTableView(TableView<SongData> tableView);

        void onViewShowing_viewer_setFilterColumn(ComboBox<SongDataProperty> comboBox);

        void onViewShowing_ocrTester_linkTableView(TableView<OcrTestData> tableView);

        void onViewShowing_ocrCacheCapturer_setupCaptureDelayLinker(SliderTextFieldLinker linker);

        void onViewShowing_ocrCacheCapturer_setupKeyInputDelayLinker(SliderTextFieldLinker linker);

        void onViewShowing_ocrCacheCapturer_setupKeyInputDurationLinker(
                SliderTextFieldLinker linker);

        void viewer_onUpdateTableFilter(String regex, SongDataProperty property);

        void checker_onValidateDatabase();

        void checker_onCompareDatabaseWithRemote();

        Path ocrTester_onSelectCacheDirectory(Stage stage);

        Path ocrTester_onSelectTessdataDirectory(Stage stage);

        void ocrTester_onStart(String cacheDirectory, String tessdataDirectory,
                String tessdataLanguage);

        void ocrTester_onStop();

        Path ocrCacheCapturer_onSelectOutputDirectory(Stage stage);

        void ocrCacheCapturer_onStart(int captureDelay, int keyInputDelay, int keyInputDuration,
                String outputDirectory);

        void ocrCacheCapturer_onStop();
    }


    interface HomeView extends MvpView<HomePresenter> {
        void viewer_updateTableFilter();

        void checker_setResultText(String value);

        void checker_validateDatabase();

        void checker_compareDatabaseWithRemote();

        void ocrTester_selectCacheDirectory();

        void ocrTester_selectTessdataDirectory();

        void ocrTester_start();

        void ocrTester_stop();

        void ocrTester_updateProgressIndicator(double value);

        void ocrCacheCapturer_selectOutputDirectory();

        void ocrCacheCapturer_start();

        void ocrCacheCapturer_stop();
    }
}
