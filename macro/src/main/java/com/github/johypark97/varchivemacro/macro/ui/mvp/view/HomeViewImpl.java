package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Home;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class HomeViewImpl extends BorderPane implements Home.View {
    private static final String FXML_PATH = "/fxml/Home.fxml";

    private static final Duration HIGHLIGHT_ANIMATION_DURATION = Duration.seconds(1);

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
    private MenuItem manualMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private Menu updateCheckMenu;

    @FXML
    private MenuItem updateCheckViewDetailMenuItem;

    @MvpPresenter
    public Home.Presenter presenter;

    private final AtomicReference<SimpleTransition> updateCheckMenuTransition =
            new AtomicReference<>();

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

        manualMenuItem.setOnAction(event -> presenter.openManualPage());

        aboutMenuItem.setOnAction(event -> presenter.showAbout());

        updateCheckViewDetailMenuItem.setOnAction(event -> {
            presenter.showUpdateCheck();
            Optional.ofNullable(updateCheckMenuTransition.getAndSet(null)).ifPresent(x -> {
                x.stop();
                Platform.runLater(() -> updateCheckMenu.setStyle(null));
            });
        });
    }

    private Function<Double, String> getHighlightStyleCreator(
            Home.UpdateCheckHightlightColor color) {
        return switch (color) {
            case GREEN -> x -> String.format("-fx-background-color: rgba(128, 255, 128, %.2f);",
                    x / 2 + 0.25);
            case RED -> x -> String.format("-fx-background-color: rgba(255, 128, 128, %.2f);",
                    x / 2 + 0.25);
        };
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

    @Override
    public void highlightUpdateCheck(Home.UpdateCheckHightlightColor color) {
        SimpleTransition transition = new SimpleTransition(HIGHLIGHT_ANIMATION_DURATION,
                x -> updateCheckMenu.setStyle(getHighlightStyleCreator(color).apply(x)));

        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.play();

        updateCheckMenuTransition.set(transition);
    }
}
