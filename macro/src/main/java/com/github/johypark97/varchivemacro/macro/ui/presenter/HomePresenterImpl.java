package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import java.io.IOException;
import java.util.Locale;
import javafx.scene.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl implements Home.HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private final HomeStage homeStage;

    @MvpView
    public Home.HomeView view;

    public HomePresenterImpl(HomeStage homeStage) {
        this.homeStage = homeStage;
    }

    @Override
    public void startView() {
        view.setSelectedLanguage(Language.getInstance().getLocale());

        homeStage.changeCenterView_modeSelector();
    }

    @Override
    public boolean stopView() {
        return true;
    }

    @Override
    public void requestStopStage() {
        homeStage.stopStage();
    }

    @Override
    public void setCenterView(Node value) {
        view.setCenterNode(value);
    }

    @Override
    public void changeLanguage(Locale locale) {
        Language language = Language.getInstance();

        try {
            Language.saveLocale(locale);
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Language changing error");
            homeStage.showError(language.getString("home.dialog.languageChange.error"), e);

            return;
        }

        String header = language.getString("home.dialog.languageChange.done.header");
        String content = language.getString("home.dialog.languageChange.done.content");
        homeStage.showInformation(header, content);
    }
}
