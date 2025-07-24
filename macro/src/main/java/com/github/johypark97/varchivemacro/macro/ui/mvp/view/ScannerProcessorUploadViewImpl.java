package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.EventDebouncer;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorUpload;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerUploadViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class ScannerProcessorUploadViewImpl extends BorderPane
        implements ScannerProcessorUpload.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorUpload.fxml";

    @FXML
    private TableView<ScannerUploadViewModel.NewRecordData> recordTableView;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, Integer> recordTableColumnSongId;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, String> recordTableColumnSongPack;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, String> recordTableColumnSongComposer;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, String> recordTableColumnSongTitle;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, ScannerUploadViewModel.RecordButton>
            recordTableColumnButton;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, ScannerUploadViewModel.RecordPattern>
            recordTableColumnPattern;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, ScannerUploadViewModel.SongRecord>
            recordTableColumnRecordBefore;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, ScannerUploadViewModel.SongRecord>
            recordTableColumnRecordAfter;

    @FXML
    private TableColumn<ScannerUploadViewModel.NewRecordData, Boolean> recordTableColumnSelect;

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

    private CheckBoxGroupController buttonFilterCheckBoxGroupController;
    private CheckBoxGroupController patternFilterCheckBoxGroupController;

    private StringBinding selectedRecordCountTextStringBinding =
            Bindings.createStringBinding(() -> "");

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
        setupRecordTableView();
        setupEntireSelector();
        setupFilterSelector();

        uploadButton.setOnAction(event -> presenter.upload());
    }

    private void setupRecordTableView() {
        recordTableView.setEditable(true);

        recordTableColumnSongId.setCellValueFactory(new PropertyValueFactory<>("songId"));
        recordTableColumnSongPack.setCellValueFactory(new PropertyValueFactory<>("songPack"));
        recordTableColumnSongComposer.setCellValueFactory(
                new PropertyValueFactory<>("songComposer"));
        recordTableColumnSongTitle.setCellValueFactory(new PropertyValueFactory<>("songTitle"));

        recordTableColumnButton.setCellValueFactory(new PropertyValueFactory<>("button"));
        recordTableColumnPattern.setCellValueFactory(new PropertyValueFactory<>("pattern"));

        recordTableColumnRecordBefore.setCellValueFactory(
                new PropertyValueFactory<>("previousRecord"));
        recordTableColumnRecordBefore.setCellFactory(param -> new SongRecordTableCell<>());

        recordTableColumnRecordAfter.setCellValueFactory(new PropertyValueFactory<>("newRecord"));
        recordTableColumnRecordAfter.setCellFactory(param -> new SongRecordTableCell<>());

        recordTableColumnSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));
        recordTableColumnSelect.setCellFactory(param -> new CheckBoxTableCell<>());
        recordTableColumnSelect.setEditable(true);
    }

    private void setupEntireSelector() {
        entireSelectorTitledPane.setAnimated(false);
        Platform.runLater(() -> {
            entireSelectorTitledPane.setExpanded(true);
            entireSelectorTitledPane.setAnimated(true);

            entireSelectorTitledPane.expandedProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (oldValue && !newValue) {
                            Platform.runLater(() -> filterSelectorTitledPane.setExpanded(true));
                        }
                    });

            filterSelectorTitledPane.expandedProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if (oldValue && !newValue) {
                            Platform.runLater(() -> entireSelectorTitledPane.setExpanded(true));
                        }
                    });
        });

        toggleAllSelectButton.setOnAction(event -> {
            List<ScannerUploadViewModel.NewRecordData> list = recordTableView.getItems();
            boolean value =
                    list.stream().filter(x -> x.selectedProperty().get()).count() != list.size();
            list.forEach(x -> x.selectedProperty().set(value));
        });
    }

    private void setupFilterSelector() {
        buttonFilterCheckBoxGroupController =
                new CheckBoxGroupController(filterSelectorAllButtonCheckBox);
        buttonFilterCheckBoxGroupController.addCheckBox(filterSelector4BCheckBox);
        buttonFilterCheckBoxGroupController.addCheckBox(filterSelector5BCheckBox);
        buttonFilterCheckBoxGroupController.addCheckBox(filterSelector6BCheckBox);
        buttonFilterCheckBoxGroupController.addCheckBox(filterSelector8BCheckBox);

        patternFilterCheckBoxGroupController =
                new CheckBoxGroupController(filterSelectorAllPatternCheckBox);
        patternFilterCheckBoxGroupController.addCheckBox(filterSelectorNmCheckBox);
        patternFilterCheckBoxGroupController.addCheckBox(filterSelectorHdCheckBox);
        patternFilterCheckBoxGroupController.addCheckBox(filterSelectorMxCheckBox);
        patternFilterCheckBoxGroupController.addCheckBox(filterSelectorScCheckBox);

        selectFilteredButton.setOnAction(event -> {
            EnumSet<ScannerUploadViewModel.RecordButton> buttonSet =
                    EnumSet.noneOf(ScannerUploadViewModel.RecordButton.class);
            if (filterSelector4BCheckBox.isSelected()) {
                buttonSet.add(ScannerUploadViewModel.RecordButton.B4);
            }
            if (filterSelector5BCheckBox.isSelected()) {
                buttonSet.add(ScannerUploadViewModel.RecordButton.B5);
            }
            if (filterSelector6BCheckBox.isSelected()) {
                buttonSet.add(ScannerUploadViewModel.RecordButton.B6);
            }
            if (filterSelector8BCheckBox.isSelected()) {
                buttonSet.add(ScannerUploadViewModel.RecordButton.B8);
            }

            EnumSet<ScannerUploadViewModel.RecordPattern> patternSet =
                    EnumSet.noneOf(ScannerUploadViewModel.RecordPattern.class);
            if (filterSelectorNmCheckBox.isSelected()) {
                patternSet.add(ScannerUploadViewModel.RecordPattern.NM);
            }
            if (filterSelectorHdCheckBox.isSelected()) {
                patternSet.add(ScannerUploadViewModel.RecordPattern.HD);
            }
            if (filterSelectorMxCheckBox.isSelected()) {
                patternSet.add(ScannerUploadViewModel.RecordPattern.MX);
            }
            if (filterSelectorScCheckBox.isSelected()) {
                patternSet.add(ScannerUploadViewModel.RecordPattern.SC);
            }

            boolean clearAll = filterSelectorAllRadioButton.isSelected();

            recordTableView.getItems().forEach(x -> {
                if (!buttonSet.contains(x.getButton()) || !patternSet.contains(x.getPattern())) {
                    x.selectedProperty().set(false);
                    return;
                }

                x.selectedProperty().set(clearAll || x.getNewRecord().maxCombo());
            });
        });
    }

    private void setupSelectedRecordCountText(
            ObservableList<ScannerUploadViewModel.NewRecordData> value) {
        String format = Language.INSTANCE.getFormatString(
                "scanner.processor.upload.selectedRecordCountLabel", value.size());

        selectedRecordCountTextStringBinding = Bindings.createStringBinding(
                () -> String.format(format,
                        value.stream().filter(x -> x.selectedProperty().get()).count()), value);

        selectedRecordCountLabel.textProperty().bind(selectedRecordCountTextStringBinding);
    }

    @Override
    public void setRecordTableItemList(ObservableList<ScannerUploadViewModel.NewRecordData> value) {
        recordTableView.setItems(value);

        setupSelectedRecordCountText(value);
    }

    @Override
    public void updateSelectedCountText() {
        if (selectedRecordCountTextStringBinding != null) {
            selectedRecordCountTextStringBinding.invalidate();
        }
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

    public static class SongRecordTableCell<T>
            extends TableCell<T, ScannerUploadViewModel.SongRecord> {
        private static final String STYLE_DEFAULT = "-fx-alignment: center-right;";
        private static final String STYLE_MAX_COMBO =
                STYLE_DEFAULT + " -fx-background-color: #C0FFC0;";
        private static final String STYLE_PERFECT =
                STYLE_DEFAULT + " -fx-background-color: #FFC0C0;";

        @Override
        protected void updateItem(ScannerUploadViewModel.SongRecord item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setStyle(null);
                setText(null);
                return;
            }

            setText(String.format("%.2f", item.rate()));

            if (item.rate() == 100) { // NOPMD
                setStyle(STYLE_PERFECT);
            } else if (item.maxCombo()) {
                setStyle(STYLE_MAX_COMBO);
            } else {
                setStyle(STYLE_DEFAULT);
            }
        }
    }


    public static class CheckBoxGroupController {
        private final CheckBox fullControlCheckBox;
        private final EventDebouncer eventDebouncer = new EventDebouncer();
        private final List<CheckBox> checkBoxList = new ArrayList<>();

        public CheckBoxGroupController(CheckBox fullControlCheckBox) {
            this.fullControlCheckBox = fullControlCheckBox;

            eventDebouncer.setCallback(this::updateFullControlCheckBox);

            setupFullControlCheckBox();
        }

        private int getSelectedCount() {
            return (int) checkBoxList.stream().filter(x -> x.selectedProperty().get()).count();
        }

        private void setupFullControlCheckBox() {
            fullControlCheckBox.setOnAction(event -> {
                boolean value = getSelectedCount() != checkBoxList.size();
                checkBoxList.forEach(x -> x.setSelected(value));
            });
        }

        private void updateFullControlCheckBox() {
            int selectedCount = getSelectedCount();

            if (selectedCount == 0) {
                fullControlCheckBox.setIndeterminate(false);
                fullControlCheckBox.setSelected(false);
            } else if (selectedCount == checkBoxList.size()) {
                fullControlCheckBox.setIndeterminate(false);
                fullControlCheckBox.setSelected(true);
            } else {
                fullControlCheckBox.setIndeterminate(true);
            }
        }

        private void addCheckBox(CheckBox checkBox) {
            checkBox.selectedProperty()
                    .addListener((observable, oldValue, newValue) -> eventDebouncer.trigger());

            checkBoxList.add(checkBox);
        }
    }
}
