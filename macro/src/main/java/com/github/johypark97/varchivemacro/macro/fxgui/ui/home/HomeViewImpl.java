package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.GlobalResource;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HomeViewImpl extends BorderPane implements HomeView {
    private static final String FXML_PATH = "/fxml/home/Home.fxml";
    private static final String GITHUB_URL = "https://github.com/johypark97/VArchiveMacro";

    private final Stage stage;

    @MvpPresenter
    public HomePresenter presenter;

    @FXML
    public MenuItem exitMenuItem;

    @FXML
    public RadioMenuItem langEnRadioMenuItem;

    @FXML
    public RadioMenuItem langKoRadioMenuItem;

    @FXML
    public MenuItem openSourceLicenseMenuItem;

    @FXML
    public MenuItem aboutMenuItem;

    @FXML
    public Tab scannerTab;

    @FXML
    public Tab macroTab;

    public HomeViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = HomeViewImpl.class.getResource(FXML_PATH);
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        try {
            Mvp.loadFxml(this, fxmlUrl,
                    x -> x.setResources(Language.getInstance().getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(this);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        stage.setScene(scene);

        stage.setOnShown(event -> presenter.onStartView());
        Mvp.hookWindowCloseRequest(stage, event -> presenter.onStopView());
    }

    @FXML
    public void initialize() {
        Locale locale = Language.getInstance().getLocale();
        if (Locale.ENGLISH.equals(locale)) {
            langEnRadioMenuItem.setSelected(true);
        } else if (Locale.KOREAN.equals(locale)) {
            langKoRadioMenuItem.setSelected(true);
        }

        exitMenuItem.setOnAction(event -> presenter.onStopView());

        langEnRadioMenuItem.setOnAction(event -> presenter.home_changeLanguage(Locale.ENGLISH));
        langKoRadioMenuItem.setOnAction(event -> presenter.home_changeLanguage(Locale.KOREAN));

        openSourceLicenseMenuItem.setOnAction(event -> presenter.home_openOpenSourceLicense());
        aboutMenuItem.setOnAction(event -> presenter.home_openAbout());
    }

    @Override
    public Window getWindow() {
        return stage;
    }

    @Override
    public void startView() {
        stage.show();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void showUpdateNotification(String currentVersion, String latestVersion, String url) {
        Language language = Language.getInstance();

        Alert alert = AlertBuilder.information()
                .setTitle(language.getString("home.dialog.updateCheck.title"))
                .setHeaderText(language.getString("home.dialog.updateCheck.updated"))
                .setOwner(stage).alert;

        VBox box = new VBox();
        box.setSpacing(5);
        {
            box.getChildren().add(new Label(
                    language.getFormatString("home.dialog.updateCheck.message", currentVersion,
                            latestVersion)));

            Hyperlink hyperlink = new Hyperlink(url);
            hyperlink.setOnAction(event -> {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
            box.getChildren().add(hyperlink);
        }
        alert.getDialogPane().setContent(box);

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void setScannerTabContent(Node value) {
        scannerTab.setContent(value);
    }

    @Override
    public void setMacroTabContent(Node value) {
        macroTab.setContent(value);
    }

    @Override
    public void home_openAbout() {
        VBox box = new VBox();
        box.setPadding(new Insets(20));
        box.setSpacing(10);
        {
            box.getChildren().add(new Label("Version: " + BuildInfo.version));
            box.getChildren().add(new Label("Build date: " + BuildInfo.date));

            HBox sourceCodeBox = new HBox();
            sourceCodeBox.setAlignment(Pos.CENTER_LEFT);
            sourceCodeBox.setSpacing(10);
            {
                sourceCodeBox.getChildren().add(new Label("Source code: "));

                TextField textField = new TextField(GITHUB_URL);
                textField.setEditable(false);
                HBox.setHgrow(textField, Priority.ALWAYS);
                sourceCodeBox.getChildren().add(textField);
            }
            box.getChildren().add(sourceCodeBox);
        }

        Alert alert = AlertBuilder.information().setOwner(stage).alert;
        alert.getDialogPane().setContent(box);

        alert.showAndWait();
    }
}
