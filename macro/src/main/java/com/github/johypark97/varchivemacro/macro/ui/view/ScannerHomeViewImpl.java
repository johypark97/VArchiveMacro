package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerHome;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ScannerHomeViewImpl extends BorderPane implements ScannerHome.ScannerHomeView {
    private static final String FXML_PATH = "/fxml/ScannerHome.fxml";

    @FXML
    private SplitPane recordViewerSplitPane;

    @FXML
    private TextField recordViewerSongFilterTextField;

    @FXML
    private Button recordViewerSongFilterResetButton;

    @FXML
    private TreeView<String> recordViewerSongTreeView;

    @FXML
    private Button recordViewerReloadRecordButton;

    @FXML
    private TextArea recordViewerSongInformationTextArea;

    @FXML
    private GridPane recordViewerSongRecordGridPane;

    @FXML
    private Button recordViewerScanButton;

    @FXML
    private VBox recordLoaderBox;

    @FXML
    private TextField recordLoaderDjNameTextField;

    @FXML
    private TextField recordLoaderAccountFileTextField;

    @FXML
    private Button recordLoaderAccountFileSelectButton;

    @FXML
    private Button recordLoaderLoadButton;

    @FXML
    private Button recordLoaderCancelButton;

    @FXML
    private VBox progressBox;

    @FXML
    private Button homeButton;

    @MvpPresenter
    public ScannerHome.ScannerHomePresenter presenter;

    public ScannerHomeViewImpl() {
        URL fxmlUrl = ScannerHomeViewImpl.class.getResource(FXML_PATH);

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
