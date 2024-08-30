package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OpenSourceLicenseViewImpl extends HBox implements OpenSourceLicenseView {
    private static final String FXML_FILE_NAME = "OpenSourceLicense.fxml";

    @MvpPresenter
    public OpenSourceLicensePresenter presenter;

    @FXML
    public ListView<String> libraryListView;

    @FXML
    public TextArea libraryLicenseTextArea;

    @FXML
    public TextField libraryUrlTextField;

    public OpenSourceLicenseViewImpl() {
        try {
            URL url = OpenSourceLicenseViewImpl.class.getResource(FXML_FILE_NAME);
            Mvp.loadFxml(this, url, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void startView() {
        presenter.onStartView();
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
