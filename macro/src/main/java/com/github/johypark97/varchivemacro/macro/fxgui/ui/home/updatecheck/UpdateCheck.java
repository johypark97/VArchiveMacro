package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;

public interface UpdateCheck {
    interface UpdateCheckPresenter extends CommonPresenter<UpdateCheckView, UpdateCheckPresenter> {
        void showError(String header, Throwable throwable);

        void checkUpdate();
    }


    interface UpdateCheckView extends MvpView<UpdateCheckView, UpdateCheckPresenter> {
        void startView();

        void clearAllMessages();

        void addMessage(String message);

        void addErrorMessage(String message, Throwable throwable);

        void addUpdatedMessage(String currentVersion, String latestVersion, String url);
    }
}
