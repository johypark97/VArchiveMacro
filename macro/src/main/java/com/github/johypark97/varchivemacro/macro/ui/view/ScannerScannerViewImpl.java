package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScanner;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ScannerScannerViewImpl extends StackPane implements ScannerScanner.ScannerScannerView {
    private static final String FXML_PATH = "/fxml/ScannerScanner.fxml";

    @FXML
    private HBox scannerBox;

    @FXML
    private TextField accountFileTextField;

    @FXML
    private CheckBox accountFileShowCheckBox;

    @FXML
    private TextField cacheDirectoryTextField;

    @FXML
    private CheckBox cacheDirectoryShowCheckBox;

    @FXML
    private Label autoAnalysisLabel;

    @FXML
    private Label startKeyLabel;

    @FXML
    private Label stopKeyLabel;

    @FXML
    private TitledPane checkerTitledPane;

    @FXML
    private Button checkButton;

    @FXML
    private ListView<String> categoryListView;

    @FXML
    private Button selectAllCategoryButton;

    @FXML
    private Button unselectAllCategoryButton;

    @FXML
    private VBox progressBox;

    @MvpPresenter
    public ScannerScanner.ScannerScannerPresenter presenter;

    public ScannerScannerViewImpl() {
        URL fxmlUrl = ScannerScannerViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
    }
}
