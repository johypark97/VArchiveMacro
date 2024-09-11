package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.GlobalResource;
import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class OpenSourceLicenseViewImpl extends HBox implements OpenSourceLicenseView {
    private static final String FXML_FILE_NAME = "OpenSourceLicense.fxml";

    private final Stage stage;

    @MvpPresenter
    public OpenSourceLicensePresenter presenter;

    @FXML
    public ListView<String> libraryListView;

    @FXML
    public TextArea libraryLicenseTextArea;

    @FXML
    public TextField libraryUrlTextField;

    public OpenSourceLicenseViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = OpenSourceLicenseViewImpl.class.getResource(FXML_FILE_NAME);
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        try {
            Mvp.loadFxml(this, fxmlUrl, null);
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
        libraryListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        presenter.showLicense(newValue);
                    }
                });
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
    public void setLibraryList(ObservableList<String> list) {
        libraryListView.setItems(list);
    }

    @Override
    public void setLicenseText(String text) {
        libraryLicenseTextArea.setText(text);
    }

    @Override
    public void setLicenseUrl(String url) {
        libraryUrlTextField.setText(url);
    }
}
