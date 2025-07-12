package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Home;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.BorderPane;

public class HomeViewImpl extends BorderPane implements Home.View {
    private static final String FXML_PATH = "/fxml/Home.fxml";

    @FXML
    private MenuItem settingMenuItem;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private RadioMenuItem langEnRadioMenuItem;

    @FXML
    private RadioMenuItem langKoRadioMenuItem;

    @FXML
    private MenuItem openSourceLicenseMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @MvpPresenter
    public Home.Presenter presenter;

    public HomeViewImpl() {
        URL fxmlUrl = HomeViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        settingMenuItem.setOnAction(event -> presenter.showSetting());
        closeMenuItem.setOnAction(event -> presenter.requestStopStage());

        langEnRadioMenuItem.setOnAction(event -> presenter.changeLanguage(Locale.ENGLISH));
        langKoRadioMenuItem.setOnAction(event -> presenter.changeLanguage(Locale.KOREAN));

        openSourceLicenseMenuItem.setOnAction(event -> presenter.showOpenSourceLicense());

        aboutMenuItem.setOnAction(event -> presenter.showAbout());
    }

    @Override
    public void setCenterNode(Node value) {
        setCenter(value);
    }

    @Override
    public void setSelectedLanguage(Locale locale) {
        if (Locale.ENGLISH.equals(locale)) {
            langEnRadioMenuItem.setSelected(true);
        } else if (Locale.KOREAN.equals(locale)) {
            langKoRadioMenuItem.setSelected(true);
        }
    }
}
