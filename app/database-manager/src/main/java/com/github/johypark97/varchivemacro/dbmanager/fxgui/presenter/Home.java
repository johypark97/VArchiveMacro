package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.OcrTestData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.lib.common.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void onSetupModel();

        void onLinkViewerTable(TableView<SongData> tableView);

        void onSetViewerTableFilterColumn(ComboBox<SongDataProperty> comboBox);

        void onUpdateViewerTableFilter(String regex, SongDataProperty property);

        void onValidateDatabase();

        void onCompareDatabaseWithRemote();

        void onLinkOcrTesterTable(TableView<OcrTestData> tableView);

        void onShowOcrTesterCacheDirectorySelector(Stage stage);

        void onShowOcrTesterTessdataDirectorySelector(Stage stage);

        void onStartOcrTester(String cacheDirectory, String tessdataDirectory,
                String tessdataLanguage);

        void onStopOcrTester();

        void onSetupOcrCacheCapturerCaptureDelayLinker(SliderTextFieldLinker linker);

        void onSetupOcrCacheCapturerKeyInputDelayLinker(SliderTextFieldLinker linker);

        void onSetupOcrCacheCapturerKeyInputDurationLinker(SliderTextFieldLinker linker);

        void onShowOcrCacheCapturerOutputDirectorySelector(Stage stage);

        boolean initialize();

        boolean terminate();
    }


    interface HomeView extends MvpView<HomePresenter> {
        void setCheckerTextAreaText(String value);

        void setOcrTesterCacheDirectoryText(String value);

        void setOcrTesterTessdataDirectoryText(String value);

        void updateOcrTesterProgressIndicator(double value);

        int getOcrCacheCapturerCaptureDelay();

        int getOcrCacheCapturerKeyInputDelay();

        int getOcrCacheCapturerKeyInputDuration();

        String getOcrCacheCapturerOutputDirectoryText();

        void setOcrCacheCapturerOutputDirectoryText(String value);
    }
}
