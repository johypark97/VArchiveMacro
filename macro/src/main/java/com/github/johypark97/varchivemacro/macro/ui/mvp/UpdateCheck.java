package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface UpdateCheck {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void requestStopStage();

        void showError(String header, String content, Throwable throwable);

        void openBrowser(String url);

        void checkUpdate();

        void updateProgramData();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void clearAllMessages();

        void addMessage(String... message);

        void addErrorMessage(String content, Throwable throwable);

        void addErrorMessage(String header, String content, Throwable throwable);

        void addNewVersionReleasedMessage(String currentVersion, String latestVersion, String url);

        void addNewPrereleaseVersionReleasedMessage(String currentVersion, String latestVersion,
                String url);

        void addProgramDataUpdatedMessage(String currentVersion, String latestVersion);

        DataUpdateProgressController addProgramDataUpdateProgressMessage();
    }


    interface DataUpdateProgressController {
        void setProgress(double value);

        void setMessage(String value);
    }
}
