package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorReview;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerReviewViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ScannerProcessorReviewViewImpl extends StackPane
        implements ScannerProcessorReview.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorReview.fxml";

    private static final Duration LINK_EDITOR_TRANSITION_DURATION = Duration.millis(200);

    private final ImageViewer imageViewer = new ImageViewer();

    @FXML
    private VBox linkTableBox;

    @FXML
    private Label linkTableFilterLabel;

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
    private Label linkTableSelectedCountLabel;

    @FXML
    private Button linkTableToggleExactButton;

    @FXML
    private Button linkTableToggleSimilarButton;

    @FXML
    private Button linkTableToggleEditedButton;

    @FXML
    private Button linkTableUnselectAllButton;

    @FXML
    private TableView<ScannerReviewViewModel.LinkTableData> linkTableView;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, Integer> linkTableColumnSongId;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, String> linkTableColumnSongTitle;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, String> linkTableColumnSongComposer;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, String> linkTableColumnSongPack;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, List<ScannerReviewViewModel.LinkedCaptureData>>
            linkTableColumnCaptureImage;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, ScannerReviewViewModel.LinkTableData.Accuracy>
            linkTableColumnStatusAccuracy;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, String> linkTableColumnStatusProblem;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, Integer> linkTableColumnEdit;

    @FXML
    private TableColumn<ScannerReviewViewModel.LinkTableData, Boolean> linkTableColumnSelect;

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
    private ListView<ScannerReviewViewModel.CaptureData> linkEditorCaptureImageListView;

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
        setupLinkTableFilterCheckBox();
        setupLinkTableButton();
        setupLinkTableView();
        setupLinkEditor();
    }

    private void setupLinkTableFilterCheckBox() {
        linkTableFilterConflictCheckBox.setSelected(true);
        linkTableFilterDuplicatedCheckBox.setSelected(true);
        linkTableFilterSimilarCheckBox.setSelected(true);

        EventHandler<ActionEvent> eventHandler = event -> presenter.updateLinkTableViewFilter();
        linkTableFilterConflictCheckBox.setOnAction(eventHandler);
        linkTableFilterDuplicatedCheckBox.setOnAction(eventHandler);
        linkTableFilterExactCheckBox.setOnAction(eventHandler);
        linkTableFilterNotDetectedCheckBox.setOnAction(eventHandler);
        linkTableFilterSimilarCheckBox.setOnAction(eventHandler);
    }

    private void setupLinkTableButton() {
        linkTableToggleExactButton.setOnAction(event -> presenter.toggleLinkTableSelected(
                ScannerProcessorReview.LinkTableToggleType.EXACT));

        linkTableToggleSimilarButton.setOnAction(event -> presenter.toggleLinkTableSelected(
                ScannerProcessorReview.LinkTableToggleType.SIMILAR));

        linkTableToggleEditedButton.setOnAction(event -> presenter.toggleLinkTableSelected(
                ScannerProcessorReview.LinkTableToggleType.EDITED));

        linkTableUnselectAllButton.setOnAction(event -> presenter.toggleLinkTableSelected(
                ScannerProcessorReview.LinkTableToggleType.UNSELECT_ALL));
    }

    private void setupLinkTableView() {
        linkTableView.setEditable(true);

        linkTableView.setPlaceholder(new Label(
                Language.INSTANCE.getString("scanner.processor.review.linkTable.placeholder")));
        linkTableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_CONFLICT = "table-row-color-red";
            private static final String STYLE_CLASS_DUPLICATED = "table-row-color-purple";
            private static final String STYLE_CLASS_EDITED = "table-row-color-blue";
            private static final String STYLE_CLASS_EXACT = "table-row-color-green";
            private static final String STYLE_CLASS_SIMILAR = "table-row-color-yellow";

            @Override
            protected void updateItem(ScannerReviewViewModel.LinkTableData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_CONFLICT, STYLE_CLASS_DUPLICATED,
                        STYLE_CLASS_EDITED, STYLE_CLASS_EXACT, STYLE_CLASS_SIMILAR);

                if (empty || item == null) {
                    return;
                }

                if (ScannerReviewViewModel.LinkTableData.Problem.EDITED.equals(
                        item.problemProperty().get())) {
                    getStyleClass().add(STYLE_CLASS_EDITED);
                    return;
                } else if (ScannerReviewViewModel.LinkTableData.Problem.DELETED.equals(
                        item.problemProperty().get())) {
                    return;
                }

                switch (item.accuracyProperty().get()) {
                    case CONFLICT -> getStyleClass().add(STYLE_CLASS_CONFLICT);
                    case DUPLICATED -> getStyleClass().add(STYLE_CLASS_DUPLICATED);
                    case EXACT -> getStyleClass().add(STYLE_CLASS_EXACT);
                    case SIMILAR -> getStyleClass().add(STYLE_CLASS_SIMILAR);
                    default -> {
                    }
                }
            }
        });

        linkTableColumnSongId.setCellValueFactory(new PropertyValueFactory<>("songId"));
        linkTableColumnSongTitle.setCellValueFactory(new PropertyValueFactory<>("songTitle"));
        linkTableColumnSongComposer.setCellValueFactory(new PropertyValueFactory<>("songComposer"));
        linkTableColumnSongPack.setCellValueFactory(new PropertyValueFactory<>("songPack"));

        linkTableColumnCaptureImage.setCellValueFactory(new PropertyValueFactory<>("captureImage"));
        linkTableColumnCaptureImage.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(List<ScannerReviewViewModel.LinkedCaptureData> item,
                    boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item.stream().map(x -> String.format("(%d) %s", x.captureData().entryId(),
                        x.captureData().scannedTitle())).collect(Collectors.joining(", ")));
            }
        });

        linkTableColumnStatusAccuracy.setCellValueFactory(new PropertyValueFactory<>("accuracy"));

        linkTableColumnStatusProblem.setCellValueFactory(new PropertyValueFactory<>("problem"));

        linkTableColumnEdit.setCellValueFactory(new PropertyValueFactory<>("songId"));
        linkTableColumnEdit.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Hyperlink hyperlink = new Hyperlink("\uD83D\uDD17");
                hyperlink.setOnAction(event -> presenter.showLinkEditor(item));
                hyperlink.setStyle("-fx-scale-x: 1.4; -fx-scale-y: 1.4;");

                setGraphic(hyperlink);
            }
        });

        linkTableColumnSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));
        linkTableColumnSelect.setCellFactory(param -> new CheckBoxTableCell<>());
        linkTableColumnSelect.setEditable(true);
    }

    private void setupLinkEditor() {
        coverPane.setVisible(false);

        linkEditorTitledPane.setDisable(true);
        linkEditorTitledPane.setVisible(false);

        linkEditorGridPane.add(imageViewer, 0, 1);
        imageViewer.getStyleClass().add("image-viewer");

        linkEditorFilterTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> presenter.updateLinkEditorCaptureImageFilter());
        linkEditorFilterResetButton.disableProperty()
                .bind(Bindings.equal(linkEditorFilterTextField.textProperty(), ""));
        linkEditorFilterResetButton.setOnAction(event -> {
            linkEditorFilterTextField.clear();
            linkEditorFilterTextField.requestFocus();
        });
        linkEditorFilterFindAllCheckBox.setOnAction(
                event -> presenter.updateLinkEditorCaptureImageFilter());

        linkEditorCaptureImageListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ScannerReviewViewModel.CaptureData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(String.format("(%d) %s", item.entryId(), item.scannedTitle()));
            }
        });
        linkEditorCaptureImageListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        presenter.showLinkEditorImage(newValue.entryId());
                    }
                });

        linkEditorLinkButton.setOnAction(event -> {
            ScannerReviewViewModel.CaptureData captureData =
                    linkEditorCaptureImageListView.getSelectionModel().getSelectedItem();
            if (captureData == null) {
                return;
            }

            presenter.linkSongAndCapture(captureData.entryId());
        });
        linkEditorUnlinkButton.setOnAction(event -> presenter.unlinkSongAndCapture());
        linkEditorCancelButton.setOnAction(event -> presenter.hideLinkEditor());
    }

    private void setupLinkTableFilterText(
            ObservableList<ScannerReviewViewModel.LinkTableData> value) {
        String format =
                Language.INSTANCE.getFormatString("scanner.processor.review.linkTable.filter.label",
                        value.size());
        linkTableFilterLabel.textProperty()
                .bind(Bindings.createStringBinding(() -> String.format(format, value.size()),
                        value));

        linkTableFilterExactCheckBox.setText(String.format("%s (%d)",
                Language.INSTANCE.getString("scanner.processor.review.linkTable.filter.exact"),
                value.stream()
                        .filter(x -> ScannerReviewViewModel.LinkTableData.Accuracy.EXACT.equals(
                                x.accuracyProperty().get())).count()));

        linkTableFilterDuplicatedCheckBox.setText(String.format("%s (%d)",
                Language.INSTANCE.getString("scanner.processor.review.linkTable.filter.duplicated"),
                value.stream()
                        .filter(x -> ScannerReviewViewModel.LinkTableData.Accuracy.DUPLICATED.equals(
                                x.accuracyProperty().get())).count()));

        linkTableFilterSimilarCheckBox.setText(String.format("%s (%d)",
                Language.INSTANCE.getString("scanner.processor.review.linkTable.filter.similar"),
                value.stream()
                        .filter(x -> ScannerReviewViewModel.LinkTableData.Accuracy.SIMILAR.equals(
                                x.accuracyProperty().get())).count()));

        linkTableFilterConflictCheckBox.setText(String.format("%s (%d)",
                Language.INSTANCE.getString("scanner.processor.review.linkTable.filter.conflict"),
                value.stream()
                        .filter(x -> ScannerReviewViewModel.LinkTableData.Accuracy.CONFLICT.equals(
                                x.accuracyProperty().get())).count()));

        linkTableFilterNotDetectedCheckBox.setText(String.format("%s (%d)",
                Language.INSTANCE.getString(
                        "scanner.processor.review.linkTable.filter.notDetected"), value.stream()
                        .filter(x -> ScannerReviewViewModel.LinkTableData.Accuracy.NOT_DETECTED.equals(
                                x.accuracyProperty().get())).count()));
    }

    @Override
    public void setLinkTableItemList(ObservableList<ScannerReviewViewModel.LinkTableData> value) {
        linkTableView.setItems(value);

        // Due to the setupLinkTableFilterText(), the value must be a non-filtered list.
        setupLinkTableFilterText(value);
    }

    @Override
    public void updateLinkTableSelectedCountText(int exactSelected, int exactTotal,
            int similarSelected, int similarTotal, int editedSelected, int editedTotal) {
        linkTableSelectedCountLabel.setText(Language.INSTANCE.getFormatString(
                "scanner.processor.review.linkEditor.selectedCountLabel", exactSelected, exactTotal,
                similarSelected, similarTotal, editedSelected, editedTotal));
    }

    @Override
    public boolean getLinkTableFilter_exact() {
        return linkTableFilterExactCheckBox.isSelected();
    }

    @Override
    public boolean getLinkTableFilter_duplicated() {
        return linkTableFilterDuplicatedCheckBox.isSelected();
    }

    @Override
    public boolean getLinkTableFilter_similar() {
        return linkTableFilterSimilarCheckBox.isSelected();
    }

    @Override
    public boolean getLinkTableFilter_conflict() {
        return linkTableFilterConflictCheckBox.isSelected();
    }

    @Override
    public boolean getLinkTableFilter_notDetected() {
        return linkTableFilterNotDetectedCheckBox.isSelected();
    }

    @Override
    public void refreshLinkTable() {
        linkTableView.refresh();
    }

    @Override
    public void showLinkEditor() {
        linkEditorTitledPane.setDisable(false);
        linkTableBox.setDisable(true);

        coverPane.setVisible(true);
        linkEditorTitledPane.setVisible(true);

        new SimpleTransition(LINK_EDITOR_TRANSITION_DURATION, x -> {
            coverPane.setOpacity(x / 2);
            linkEditorTitledPane.setOpacity(x);
        }).play();
    }

    @Override
    public void hideLinkEditor() {
        linkEditorTitledPane.setDisable(true);
        linkTableBox.setDisable(false);

        SimpleTransition transition = new SimpleTransition(LINK_EDITOR_TRANSITION_DURATION, x -> {
            coverPane.setOpacity((1 - x) / 2);
            linkEditorTitledPane.setOpacity(1 - x);
        });

        transition.setOnFinished(event -> {
            coverPane.setVisible(false);
            linkEditorTitledPane.setVisible(false);
        });

        transition.play();
    }

    @Override
    public void resetLinkEditorImageAndFilter() {
        imageViewer.clearImage();
        linkEditorFilterFindAllCheckBox.setSelected(false);
        linkEditorFilterTextField.clear();
    }

    @Override
    public String getLinkEditorFilterText() {
        return linkEditorFilterTextField.getText();
    }

    @Override
    public boolean isLinkEditorFindAllChecked() {
        return linkEditorFilterFindAllCheckBox.isSelected();
    }

    @Override
    public void setLinkEditorSelectedSongText(String value) {
        linkEditorSelectedSongTextField.setText(value);
    }

    @Override
    public void setLinkEditorCaptureImageItemList(
            ObservableList<ScannerReviewViewModel.CaptureData> value) {
        linkEditorCaptureImageListView.setItems(value);
    }

    @Override
    public void setLinkEditorImage(Image value) {
        imageViewer.setImage(value);
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getLeftButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.review.frameButton.reset"),
                event -> presenter.runLinking());
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getRightButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.review.frameButton.analyze"),
                event -> presenter.showAnalysisView());
    }
}
