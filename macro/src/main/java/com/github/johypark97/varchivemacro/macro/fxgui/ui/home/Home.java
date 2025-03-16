package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.CommonMvp.CommonView;
import java.util.Locale;
import javafx.scene.Node;

public interface Home {
    enum TabHighlightType {
        GREEN,
        RED
    }


    interface HomePresenter extends CommonPresenter<HomeView, HomePresenter> {
        void home_changeLanguage(Locale locale);

        void home_openOpenSourceLicense();

        void home_openAbout();
    }


    interface HomeView extends CommonView<HomeView, HomePresenter> {
        void startView();

        void showError(String header, Throwable throwable);

        void showInformation(String header, String content);

        boolean showConfirmation(String header, String content);

        void setScannerTabContent(Node value);

        void setMacroTabContent(Node value);

        void setUpdateCheckTabContent(Node value);

        void home_openAbout(String buildDate, String programVersion, String dataVersion);

        void highlightUpdateCheckTab(TabHighlightType type);
    }
}
