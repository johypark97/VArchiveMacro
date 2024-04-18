package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasMany;
import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasOne;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.LinkMetadata;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ScannerComponent extends TabPane {
    private static final String FXML_FILE_NAME = "Scanner.fxml";

    private final WeakReference<HomeView> viewReference;

    @FXML
    public TextField viewer_filterTextField;

    @FXML
    public Button viewer_filterResetButton;

    @FXML
    public TreeView<ViewerTreeData> viewer_songTreeView;

    @FXML
    public TextArea viewer_informationTextArea;

    @FXML
    public GridPane viewer_recordGridPane;

    @FXML
    public TableView<CaptureData> capture_captureTableView;

    @FXML
    public Button capture_showButton;

    @FXML
    public ListView<CaptureTabListData> capture_tabListView;

    @FXML
    public Button capture_selectAllTabButton;

    @FXML
    public Button capture_unselectAllTabButton;

    @FXML
    public TableView<SongData> song_songTableView;

    @FXML
    public Button song_editButton;

    @FXML
    public TextField option_cacheDirectoryTextField;

    @FXML
    public Button option_cacheDirectorySelectButton;

    @FXML
    public Slider option_captureDelaySlider;

    @FXML
    public TextField option_captureDelayTextField;

    @FXML
    public Slider option_keyInputDurationSlider;

    @FXML
    public TextField option_keyInputDurationTextField;

    @FXML
    public TextField option_accountFileTextField;

    @FXML
    public Button option_accountFileSelectButton;

    @FXML
    public Slider option_recordUploadDelaySlider;

    @FXML
    public TextField option_recordUploadDelayTextField;

    public SliderTextFieldLinker optionCaptureDelayLinker;
    public SliderTextFieldLinker optionKeyInputDurationLinker;
    public SliderTextFieldLinker optionRecordUploadDelayLinker;

    private ViewerRecordController viewerRecordController;

    public ScannerComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = ScannerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setVisible(false);

        setupViewer();
        setupCapture();
        setupSong();
        setupOption();
    }

    public void viewer_showInformation(String title, String composer) {
        StringBuilder builder = new StringBuilder();

        builder.append("Title: ").append(title).append(System.lineSeparator());
        builder.append("Composer: ").append(composer);

        viewer_informationTextArea.setText(builder.toString());
    }

    public void viewer_resetRecord(int row, int column) {
        viewerRecordController.clearCell(row, column);
    }

    public void viewer_setRecord(int row, int column, float rate, boolean maxCombo) {
        viewerRecordController.setCell(row, column, rate, maxCombo);
    }

    public void viewer_shadowRecord(int row, int column) {
        viewerRecordController.shadowCell(row, column);
    }

    public void capture_setTabList(List<String> list) {
        ObservableList<CaptureTabListData> observableList =
                list.stream().map(CaptureTabListData::new)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

        capture_tabListView.setItems(observableList);
    }

    public Set<String> capture_getSelectedTabSet() {
        return capture_tabListView.getItems().stream().filter(x -> x.checked.get()).map(x -> x.name)
                .collect(Collectors.toSet());
    }

    public void capture_setSelectedTabSet(Set<String> value) {
        capture_tabListView.getItems().forEach(x -> x.checked.set(value.contains(x.name)));
    }

    public String option_getCacheDirectory() {
        return option_cacheDirectoryTextField.getText();
    }

    public void option_setCacheDirectory(String value) {
        option_cacheDirectoryTextField.setText(value);
    }

    public int option_getCaptureDelay() {
        return optionCaptureDelayLinker.getValue();
    }

    public int option_getKeyInputDuration() {
        return optionKeyInputDurationLinker.getValue();
    }

    public String option_getAccountFile() {
        return option_accountFileTextField.getText();
    }

    public void option_setAccountFile(String value) {
        option_accountFileTextField.setText(value);
    }

    public int option_getRecordUploadDelay() {
        return optionRecordUploadDelayLinker.getValue();
    }

    public void capture_setCaptureDataList(ObservableList<CaptureData> list) {
        capture_captureTableView.setItems(list);
    }

    public void capture_refresh() {
        capture_captureTableView.refresh();
    }

    public void song_setSongDataList(ObservableList<SongData> list) {
        song_songTableView.setItems(list);
    }

    public void song_refresh() {
        song_songTableView.refresh();
    }

    private HomeView getView() {
        return viewReference.get();
    }

    private void openCaptureViewer() {
        CaptureData selected = capture_captureTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            getView().scanner_capture_openCaptureViewer(selected.idProperty().get());
        }
    }

    private void openLinkEditor() {
        SongData selected = song_songTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            getView().scanner_song_openLinkEditor(selected.idProperty().get());
        }
    }

    private void setupViewer() {
        viewer_filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if (!value.equals(oldValue.trim())) {
                getView().scanner_viewer_showSongTree(newValue.trim());
            }
        });

        viewer_filterResetButton.setOnAction(event -> viewer_filterTextField.clear());

        setupViewer_treeView();

        viewerRecordController = new ViewerRecordController(viewer_recordGridPane);
        viewerRecordController.setupTransposeButton();
    }

    private void setupViewer_treeView() {
        viewer_songTreeView.setShowRoot(false);

        viewer_songTreeView.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(ViewerTreeData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item.name != null) {
                    setText(item.name);
                    return;
                }

                setText(String.format("%s ...... %s", item.song.title, item.song.composer));
            }
        });

        viewer_songTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        return;
                    }

                    ViewerTreeData data = newValue.getValue();
                    if (data.song == null) {
                        return;
                    }

                    getView().scanner_viewer_showRecord(data.song.id);
                });
    }

    private void setupCapture() {
        setupCapture_captureTableView();

        capture_showButton.setOnAction(event -> openCaptureViewer());

        capture_tabListView.setCellFactory(CheckBoxListCell.forListView(param -> param.checked));

        capture_selectAllTabButton.setOnAction(
                event -> capture_tabListView.getItems().forEach(x -> x.checked.set(true)));

        capture_unselectAllTabButton.setOnAction(
                event -> capture_tabListView.getItems().forEach(x -> x.checked.set(false)));
    }

    private void setupCapture_captureTableView() {
        capture_captureTableView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openCaptureViewer();
            }
        });

        TableColumn<CaptureData, Integer> id = new TableColumn<>("Capture Data Id");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<CaptureData, String> scannedTitle = new TableColumn<>("Scanned Title");
        scannedTitle.setCellValueFactory(new PropertyValueFactory<>("scannedTitle"));
        scannedTitle.setComparator(new TitleComparator());
        scannedTitle.setPrefWidth(250);

        TableColumn<CaptureData, List<SongData>> linkedSongs = new TableColumn<>("Linked Songs");
        linkedSongs.setCellValueFactory(new PropertyValueFactory<>("parentList"));
        linkedSongs.setPrefWidth(150);
        linkedSongs.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(List<SongData> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item.stream().map(x -> {
                    String songTitle = x.songProperty().get().title;
                    int id = x.idProperty().get();

                    return String.format("(%d) %s", id, songTitle);
                }).collect(Collectors.joining(", ")));
            }
        });

        TableColumn<CaptureData, Exception> error = new TableColumn<>("Error");
        error.setCellValueFactory(new PropertyValueFactory<>("exception"));
        error.setPrefWidth(100);
        error.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Exception item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }

                setText(item.getMessage());

                Tooltip tooltip = new Tooltip(item.toString());
                tooltip.setShowDelay(Duration.millis(200));
                setTooltip(tooltip);
            }
        });

        capture_captureTableView.getColumns().addAll(List.of(id, scannedTitle, linkedSongs, error));

        capture_captureTableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_EXACT = "table-row-color-green";
            private static final String STYLE_CLASS_WARNING = "table-row-color-red";
            private static final String STYLE_CLASS_SIMILAR = "table-row-color-yellow";

            @Override
            protected void updateItem(CaptureData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_EXACT, STYLE_CLASS_SIMILAR,
                        STYLE_CLASS_WARNING);

                if (empty || item == null) {
                    return;
                }

                if (hasOne(item.parentListProperty())) {
                    SongData songData = item.parentListProperty().get(0);
                    LinkMetadata linkMetadata = songData.linkMapProperty().get(item);

                    getStyleClass().add((linkMetadata.distanceProperty().get() == 0)
                            ? STYLE_CLASS_EXACT
                            : STYLE_CLASS_SIMILAR);
                } else if (hasMany(item.parentListProperty())) {
                    getStyleClass().add(STYLE_CLASS_WARNING);
                }
            }
        });
    }

    private void setupSong() {
        setupSong_songTableView();

        song_editButton.setOnAction(event -> openLinkEditor());
    }

    private void setupSong_songTableView() {
        song_songTableView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openLinkEditor();
            }
        });

        TableColumn<SongData, Integer> id = new TableColumn<>("Song Data Id");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<SongData, ?> song = new TableColumn<>("Song");
        {
            TableColumn<SongData, LocalDlcSong> title = new TableColumn<>("Title");
            title.setCellValueFactory(new PropertyValueFactory<>("song"));
            title.setPrefWidth(200);
            title.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDlcSong item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.title);
                }
            });
            title.setComparator(new Comparator<>() {
                private final TitleComparator titleComparator = new TitleComparator();

                @Override
                public int compare(LocalDlcSong o1, LocalDlcSong o2) {
                    return titleComparator.compare(o1.title, o2.title);
                }
            });

            TableColumn<SongData, LocalDlcSong> composer = new TableColumn<>("Composer");
            composer.setCellValueFactory(new PropertyValueFactory<>("song"));
            composer.setPrefWidth(150);
            composer.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDlcSong item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.composer);
                }
            });

            TableColumn<SongData, LocalDlcSong> dlc = new TableColumn<>("DLC");
            dlc.setCellValueFactory(new PropertyValueFactory<>("song"));
            dlc.setPrefWidth(150);
            dlc.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDlcSong item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.dlc);
                }
            });

            song.getColumns().addAll(List.of(title, composer, dlc));
        }

        TableColumn<SongData, Map<CaptureData, LinkMetadata>> linkedCaptures =
                new TableColumn<>("Linked Captures");
        linkedCaptures.setCellValueFactory(new PropertyValueFactory<>("linkMap"));
        linkedCaptures.setPrefWidth(200);
        linkedCaptures.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Map<CaptureData, LinkMetadata> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item.entrySet().stream().map(x -> {
                    String scannedTitle = x.getKey().scannedTitle.get();
                    double accuracy = x.getValue().accuracyProperty().get();
                    int distance = x.getValue().distanceProperty().get();
                    int id = x.getKey().idProperty().get();

                    return String.format("(%d) %s [%d, %.2f%%]", id, scannedTitle, distance,
                            accuracy * 100);
                }).collect(Collectors.joining(", ")));
            }
        });

        TableColumn<SongData, Boolean> select = new TableColumn<>("Select");
        select.setCellFactory(CheckBoxTableCell.forTableColumn(select));
        select.setCellValueFactory(new PropertyValueFactory<>("selected"));
        select.setPrefWidth(100);

        song_songTableView.getColumns().addAll(List.of(id, song, linkedCaptures, select));

        song_songTableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_CHANGED = "table-row-color-blue";
            private static final String STYLE_CLASS_EXACT = "table-row-color-green";
            private static final String STYLE_CLASS_INVALID = "table-row-color-red";
            private static final String STYLE_CLASS_SIMILAR = "table-row-color-yellow";

            @Override
            protected void updateItem(SongData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_CHANGED, STYLE_CLASS_EXACT,
                        STYLE_CLASS_INVALID, STYLE_CLASS_SIMILAR);

                if (empty || item == null) {
                    return;
                }

                if (hasOne(item.childListProperty())) {
                    if (item.linkChanged.get()) {
                        getStyleClass().add(STYLE_CLASS_CHANGED);
                    } else if (item.linkExact.get()) {
                        getStyleClass().add(STYLE_CLASS_EXACT);
                    } else {
                        getStyleClass().add(STYLE_CLASS_SIMILAR);
                    }
                } else if (hasMany(item.childListProperty())) {
                    getStyleClass().add(STYLE_CLASS_INVALID);
                }
            }
        });
    }

    private void setupOption() {
        option_cacheDirectorySelectButton.setOnAction(
                event -> getView().scanner_option_openCacheDirectorySelector());

        optionCaptureDelayLinker =
                new SliderTextFieldLinker(option_captureDelaySlider, option_captureDelayTextField);

        optionKeyInputDurationLinker = new SliderTextFieldLinker(option_keyInputDurationSlider,
                option_keyInputDurationTextField);

        option_accountFileSelectButton.setOnAction(
                event -> getView().scanner_option_openAccountFileSelector());

        optionRecordUploadDelayLinker = new SliderTextFieldLinker(option_recordUploadDelaySlider,
                option_recordUploadDelayTextField);
    }

    public static class ViewerRecordController {
        private final WeakReference<GridPane> gridPaneReference;

        public ViewerRecordController(GridPane gridPane) {
            gridPaneReference = new WeakReference<>(gridPane);
        }

        public void setupTransposeButton() {
            getTransposeButton().setOnAction(event -> {
                int count = getGridPane().getChildren().size();
                for (int i = 0; i < count; ++i) {
                    Node node = getGridPane().getChildren().get(i);

                    int column = Objects.requireNonNullElse(GridPane.getColumnIndex(node), 0);
                    int row = Objects.requireNonNullElse(GridPane.getRowIndex(node), 0);

                    if (row != count) {
                        GridPane.setColumnIndex(node, row);
                        GridPane.setRowIndex(node, column);
                    }
                }
            });
        }

        public void clearCell(int row, int column) {
            TextField textField = getTextField(row, column);

            textField.clear();
            textField.setDisable(false);
            textField.setEffect(null);
            textField.setStyle("");
        }

        public void setCell(int row, int column, float rate, boolean maxCombo) {
            TextField textField = getTextField(row, column);
            if (rate == 0) {
                textField.setText("-");
                return;
            }

            textField.setText(String.format("%.2f", rate));

            if (rate == 100) { // NOPMD
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.RED);

                textField.setEffect(shadow);
                textField.setStyle("-fx-background-color: #FFF0F0;");
            } else if (maxCombo) {
                DropShadow shadow = new DropShadow();
                shadow.setColor(Color.LIME);

                textField.setEffect(shadow);
                textField.setStyle("-fx-background-color: #F0FFF0;");
            }
        }

        public void shadowCell(int row, int column) {
            getTextField(row, column).setDisable(true);
        }

        private GridPane getGridPane() {
            return gridPaneReference.get();
        }

        private Button getTransposeButton() {
            return (Button) getGridPane().getChildren().get(0);
        }

        private TextField getTextField(int row, int column) {
            if (row < 0 || row > 3 || column < 0 || column > 3) {
                throw new IllegalArgumentException();
            }

            return (TextField) getGridPane().getChildren().get((row + 1) * 5 + column + 1);
        }
    }


    public static class CaptureTabListData {
        private final BooleanProperty checked = new SimpleBooleanProperty();
        private final String name;

        public CaptureTabListData(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
