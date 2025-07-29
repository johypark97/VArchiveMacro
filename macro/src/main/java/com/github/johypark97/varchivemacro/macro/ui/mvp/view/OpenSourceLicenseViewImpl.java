package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.OpenSourceLicense;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class OpenSourceLicenseViewImpl extends HBox implements OpenSourceLicense.View {
    private static final String FXML_PATH = "/fxml/OpenSourceLicense.fxml";

    @FXML
    private ListView<String> libraryListView;

    @FXML
    private TextArea licenseTextArea;

    @FXML
    private Label copyrightOwnerLabel;

    @FXML
    private Hyperlink libraryUrlHyperlink;

    @MvpPresenter
    public OpenSourceLicense.Presenter presenter;

    public OpenSourceLicenseViewImpl() {
        URL fxmlUrl = OpenSourceLicenseViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
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

        libraryUrlHyperlink.setVisible(false);

        libraryUrlHyperlink.setOnAction(
                event -> presenter.openWebBrowser(libraryUrlHyperlink.getText()));
    }

    @Override
    public void setLibraryList(List<String> value) {
        libraryListView.setItems(FXCollections.observableList(value).sorted());
    }

    @Override
    public void setLicenseText(String value) {
        licenseTextArea.setText(value);
    }

    @Override
    public void setCopyrightOwner(String value) {
        copyrightOwnerLabel.setText(value);
    }

    @Override
    public void setLibraryUrl(String value) {
        libraryUrlHyperlink.setText(value);
        libraryUrlHyperlink.setVisible(true);
    }
}
