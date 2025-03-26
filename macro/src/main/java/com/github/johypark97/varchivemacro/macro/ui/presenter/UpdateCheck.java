package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import java.util.function.Consumer;

public interface UpdateCheck {
    interface UpdateCheckPresenter extends CommonPresenter<UpdateCheckView, UpdateCheckPresenter> {
        void showError(String header, Throwable throwable);

        void checkUpdate();

        void updateData(Consumer<Boolean> disableUpdateButton);
    }


    interface UpdateCheckView extends MvpView<UpdateCheckView, UpdateCheckPresenter> {
        void startView();

        void clearAllMessages();

        void addMessage(String message);

        void addErrorMessage(String message, Throwable throwable);

        void addProgramUpdatedMessage(String currentVersion, String latestVersion, String url);

        void addDataUpdatedMessage(long currentVersion, long latestVersion);

        DataUpdateProgressController addDataUpdateProgressMessage();

        void addDataUpdateCompleteMessage(String header, String message);
    }


    interface DataUpdateProgressController {
        void setProgress(double value);

        void setMessage(String value);
    }
}
