package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import java.lang.ref.WeakReference;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class OpenSourceLicenseComponent extends HBox {
    private static final String FXML_FILE_NAME = "OpenSourceLicense.fxml";

    private final WeakReference<OpenSourceLicenseView> viewReference;

    @FXML
    public ListView<String> libraryListView;

    @FXML
    public TextArea libraryLicenseTextArea;

    @FXML
    public TextField libraryUrlTextField;

    public OpenSourceLicenseComponent(OpenSourceLicenseView view) {
        viewReference = new WeakReference<>(view);

        URL url = OpenSourceLicenseComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        libraryListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        getView().showLicenseText(newValue);
                        getView().showLibraryUrl(newValue);
                    }
                });
    }

    public void setLibraryList(ObservableList<String> list) {
        libraryListView.setItems(list);
    }

    public void setLicenseText(String text) {
        libraryLicenseTextArea.setText(text);
    }

    public void setLibraryUrl(String url) {
        libraryUrlTextField.setText(url);
    }

    private OpenSourceLicenseView getView() {
        return viewReference.get();
    }
}
