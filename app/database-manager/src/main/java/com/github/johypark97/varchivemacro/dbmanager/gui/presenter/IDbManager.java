package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.CacheGeneratorConfig;
import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel.SongViewModel;

public interface IDbManager {
    interface Presenter {
        void start();

        void stop();

        void viewOpened();

        void loadDatabase(String path);

        void validateDatabase();

        void checkRemote();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void setViewModels(SongViewModel songViewModel);

        void showView();

        void disposeView();

        void showErrorDialog(String message);

        void setValidatorResultText(String value);

        void setCheckerResultText(String value);

        CacheGeneratorConfig getCacheGeneratorConfig();
    }
}
