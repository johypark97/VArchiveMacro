package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import java.util.Locale;
import javafx.scene.Node;

public interface Home {
    enum UpdateCheckHightlightColor {
        GREEN,
        RED
    }


    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void requestStopStage();

        void setCenterView(Node value);

        void changeLanguage(Locale locale);

        void showSetting();

        void showOpenSourceLicense();

        void openManualPage();

        void showAbout();

        void showUpdateCheck();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setCenterNode(Node value);

        void setSelectedLanguage(Locale locale);

        void highlightUpdateCheck(UpdateCheckHightlightColor color);
    }
}
