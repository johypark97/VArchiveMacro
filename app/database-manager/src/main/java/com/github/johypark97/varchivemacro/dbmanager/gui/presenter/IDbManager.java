package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.CacheGeneratorConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.GroundTruthGeneratorConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.OcrTesterConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.OcrTesterViewModel;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModel;

public interface IDbManager {
    interface Presenter {
        void start();

        void stop();

        void viewOpened();

        void loadDatabase(String path);

        void validateDatabase();

        void checkRemote();

        void generateGroundTruth();

        void runOcrTest();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void setViewModels(SongViewModel songViewModel, OcrTesterViewModel ocrTesterViewModel);

        void showView();

        void disposeView();

        void showMessageDialog(String message);

        void showErrorDialog(String message);

        void setValidatorResultText(String value);

        void setCheckerResultText(String value);

        CacheGeneratorConfig getCacheGeneratorConfig();

        GroundTruthGeneratorConfig getGroundTruthGeneratorConfig();

        OcrTesterConfig getOcrTesterConfig();
    }
}
