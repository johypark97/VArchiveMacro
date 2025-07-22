package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;

public class ScannerProcessorUploadViewImpl extends BorderPane
        implements ScannerProcessorUpload.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorUpload.fxml";

    @FXML
    private TableView recordTableView;

    @FXML
    private TableColumn recordTableColumnSongId;

    @FXML
    private TableColumn recordTableColumnSongPack;

    @FXML
    private TableColumn recordTableColumnSongComposer;

    @FXML
    private TableColumn recordTableColumnSongTitle;

    @FXML
    private TableColumn recordTableColumnButton;

    @FXML
    private TableColumn recordTableColumnPattern;

    @FXML
    private TableColumn recordTableColumnRecordBefore;

    @FXML
    private TableColumn recordTableColumnRecordAfter;

    @FXML
    private TableColumn recordTableColumnSelect;

    @FXML
    private TitledPane entireSelectorTitledPane;

    @FXML
    private Button toggleAllSelectButton;

    @FXML
    private TitledPane filterSelectorTitledPane;

    @FXML
    private CheckBox filterSelectorAllButtonCheckBox;

    @FXML
    private CheckBox filterSelector4BCheckBox;

    @FXML
    private CheckBox filterSelector5BCheckBox;

    @FXML
    private CheckBox filterSelector6BCheckBox;

    @FXML
    private CheckBox filterSelector8BCheckBox;

    @FXML
    private CheckBox filterSelectorAllPatternCheckBox;

    @FXML
    private CheckBox filterSelectorNmCheckBox;

    @FXML
    private CheckBox filterSelectorHdCheckBox;

    @FXML
    private CheckBox filterSelectorMxCheckBox;

    @FXML
    private CheckBox filterSelectorScCheckBox;

    @FXML
    private RadioButton filterSelectorAllRadioButton;

    @FXML
    private RadioButton filterSelectorMaxComboRadioButton;

    @FXML
    private Button selectFilteredButton;

    @FXML
    private Label selectedRecordCountLabel;

    @FXML
    private Button uploadButton;

    @MvpPresenter
    public ScannerProcessorUpload.Presenter presenter;

    public ScannerProcessorUploadViewImpl() {
        URL fxmlUrl = ScannerProcessorUploadViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getLeftButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.upload.frameButton.back"),
                event -> presenter.showAnalysisView());
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getRightButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.upload.frameButton.resetAndRefresh"),
                event -> presenter.collectNewRecord());
    }
}
