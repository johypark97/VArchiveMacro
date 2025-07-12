package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerProcessorReview;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ScannerProcessorReviewViewImpl extends StackPane
        implements ScannerProcessorReview.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorReview.fxml";

    private final ImageViewer imageViewer = new ImageViewer();

    @FXML
    private VBox linkTableBox;

    @FXML
    private CheckBox linkTableFilterExactCheckBox;

    @FXML
    private CheckBox linkTableFilterDuplicatedCheckBox;

    @FXML
    private CheckBox linkTableFilterSimilarCheckBox;

    @FXML
    private CheckBox linkTableFilterConflictCheckBox;

    @FXML
    private CheckBox linkTableFilterNotDetectedCheckBox;

    @FXML
    private Button linkTableToggleExactButton;

    @FXML
    private Button linkTableToggleSimilarButton;

    @FXML
    private Button linkTableToggleEditedButton;

    @FXML
    private Button linkTableUnselectAllButton;

    @FXML
    private TableView linkTableView;

    @FXML
    private TableColumn linkTableColumnSongId;

    @FXML
    private TableColumn linkTableColumnSongTitle;

    @FXML
    private TableColumn linkTableColumnSongComposer;

    @FXML
    private TableColumn linkTableColumnSongPack;

    @FXML
    private TableColumn linkTableColumnCaptureImage;

    @FXML
    private TableColumn linkTableColumnStatusAccuracy;

    @FXML
    private TableColumn linkTableColumnStatusProblem;

    @FXML
    private TableColumn linkTableColumnEdit;

    @FXML
    private TableColumn linkTableColumnSelect;

    @FXML
    private Pane coverPane;

    @FXML
    private TitledPane linkEditorTitledPane;

    @FXML
    private GridPane linkEditorGridPane;

    @FXML
    private TextField linkEditorSelectedSongTextField;

    @FXML
    private TextField linkEditorFilterTextField;

    @FXML
    private Button linkEditorFilterResetButton;

    @FXML
    private CheckBox linkEditorFilterFindAllCheckBox;

    @FXML
    private ListView linkEditorCaptureImageListView;

    @FXML
    private Button linkEditorLinkButton;

    @FXML
    private Button linkEditorUnlinkButton;

    @FXML
    private Button linkEditorCancelButton;

    @MvpPresenter
    public ScannerProcessorReview.Presenter presenter;

    public ScannerProcessorReviewViewImpl() {
        URL fxmlUrl = ScannerProcessorReviewViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        linkEditorGridPane.add(imageViewer, 0, 1);
        imageViewer.getStyleClass().add("image-viewer");
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getLeftButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.review.frameButton.reset"),
                event -> {
                });
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getRightButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.review.frameButton.analyze"),
                event -> {
                });
    }
}
