package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

public class HomeComponent extends BorderPane {
    private static final String FXML_FILE_NAME = "Home.fxml";

    private final WeakReference<HomeView> viewReference;

    @FXML
    public RadioMenuItem langEnRadioMenuItem;

    @FXML
    public RadioMenuItem langKoRadioMenuItem;

    @FXML
    public MenuItem openSourceLicenseMenuItem;

    @FXML
    public Tab scannerTab;

    public HomeComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = HomeComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    @FXML
    public void initialize() {
        Locale locale = Language.getInstance().getLocale();

        if (Locale.ENGLISH.equals(locale)) {
            langEnRadioMenuItem.setSelected(true);
        } else if (Locale.KOREAN.equals(locale)) {
            langKoRadioMenuItem.setSelected(true);
        }

        langEnRadioMenuItem.setOnAction(event -> getView().home_changeLanguage(Locale.ENGLISH));
        langKoRadioMenuItem.setOnAction(event -> getView().home_changeLanguage(Locale.KOREAN));

        openSourceLicenseMenuItem.setOnAction(event -> getView().home_openOpenSourceLicense());
    }

    private HomeView getView() {
        return viewReference.get();
    }
}
