package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasMany;
import static com.github.johypark97.varchivemacro.lib.common.CollectionUtility.hasOne;

import com.github.johypark97.varchivemacro.lib.jfx.fxgui.SliderTextFieldLinker;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.scanner.database.RecordManager.LocalRecord;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.DlcLocalSongComparator;
import com.github.johypark97.varchivemacro.lib.scanner.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.LinkMetadata;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.resource.Language;
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
import javafx.geometry.Pos;
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
import javafx.scene.control.TreeItem;
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
    public Button capture_clearButton;

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
    public Button song_toggleExactButton;

    @FXML
    public Button song_toggleSimilarButton;

    @FXML
    public Button song_toggleEditedButton;

    @FXML
    public Button song_unselectAllButton;

    @FXML
    public TableView<AnalysisData> analysis_analysisTableView;

    @FXML
    public Button analysis_showButton;

    @FXML
    public Button analysis_startButton;

    @FXML
    public Button analysis_stopButton;

    @FXML
    public Button analysis_clearButton;

    @FXML
    public TableView<NewRecordData> uploader_recordTableView;

    @FXML
    public Button uploader_refreshButton;

    @FXML
    public Button uploader_selectAllButton;

    @FXML
    public Button uploader_unselectAllButton;

    @FXML
    public Button uploader_startUploadButton;

    @FXML
    public Button uploader_stopUploadButton;

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

    private SliderTextFieldLinker optionCaptureDelayLinker;
    private SliderTextFieldLinker optionKeyInputDurationLinker;
    private SliderTextFieldLinker optionRecordUploadDelayLinker;

    private ViewerRecordController viewerRecordController;

    public ScannerComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = ScannerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    @FXML
    public void initialize() {
        setupViewer();
        setupCapture();
        setupSong();
        setupAnalysis();
        setupUploader();
        setupOption();
    }

    public void viewer_setSongTreeViewRoot(TreeItem<ViewerTreeData> root) {
        viewer_songTreeView.setRoot(root);
    }

    public void viewer_showInformation(String title, String composer) {
        StringBuilder builder = new StringBuilder(32);

        builder.append("Title: ").append(title).append("\nComposer: ").append(composer);

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

    public void setAnalysisDataList(ObservableList<AnalysisData> list) {
        analysis_analysisTableView.setItems(list);
    }

    public void setNewRecordDataList(ObservableList<NewRecordData> list) {
        uploader_recordTableView.setItems(list);
    }

    public SliderTextFieldLinker.Initializer optionCaptureDelayLinkerInitializer() {
        return optionCaptureDelayLinker.getInitializer();
    }

    public SliderTextFieldLinker.Initializer optionKeyInputDurationLinkerInitializer() {
        return optionKeyInputDurationLinker.getInitializer();
    }

    public SliderTextFieldLinker.Initializer optionRecordUploadDelayLinkerInitializer() {
        return optionRecordUploadDelayLinker.getInitializer();
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

    private void openAnalysisDataViewer() {
        AnalysisData selected = analysis_analysisTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            getView().scanner_analysis_openAnalysisDataViewer(selected.idProperty().get());
        }
    }

    private void setupViewer() {
        viewer_filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if (!value.equals(oldValue.trim())) {
                getView().scanner_viewer_updateSongTreeViewFilter(newValue.trim());
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

        capture_clearButton.setOnAction(event -> getView().scanner_capture_clearScanData());

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

        Language language = Language.getInstance();

        TableColumn<CaptureData, Integer> id =
                new TableColumn<>(language.getString("scanner.capture.table.captureDataId"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<CaptureData, String> scannedTitle =
                new TableColumn<>(language.getString("scanner.capture.table.scannedTitle"));
        scannedTitle.setCellValueFactory(new PropertyValueFactory<>("scannedTitle"));
        scannedTitle.setComparator(new TitleComparator());
        scannedTitle.setPrefWidth(250);

        TableColumn<CaptureData, List<SongData>> linkedSongs =
                new TableColumn<>(language.getString("scanner.capture.table.linkedSongs"));
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

        TableColumn<CaptureData, Exception> error =
                new TableColumn<>(language.getString("scanner.capture.table.error"));
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

        song_toggleExactButton.setOnAction(event -> {
            List<SongData> list = song_songTableView.getItems().filtered(x -> x.linkExact.get());

            for (SongData item : list) {
                if (!item.selected.get()) {
                    list.forEach(x -> x.selected.set(true));
                    return;
                }
            }

            list.forEach(x -> x.selected.set(false));
        });

        song_toggleSimilarButton.setOnAction(event -> {
            List<SongData> list = song_songTableView.getItems().filtered(
                    x -> !x.linkExact.get() && !x.linkChanged.get() && hasOne(
                            x.childListProperty()));

            for (SongData item : list) {
                if (!item.selected.get()) {
                    list.forEach(x -> x.selected.set(true));
                    return;
                }
            }

            list.forEach(x -> x.selected.set(false));
        });

        song_toggleEditedButton.setOnAction(event -> {
            List<SongData> list = song_songTableView.getItems().filtered(x -> x.linkChanged.get());

            for (SongData item : list) {
                if (!item.selected.get()) {
                    list.forEach(x -> x.selected.set(true));
                    return;
                }
            }

            list.forEach(x -> x.selected.set(false));
        });

        song_unselectAllButton.setOnAction(
                event -> song_songTableView.getItems().forEach(x -> x.selected.set(false)));
    }

    private void setupSong_songTableView() {
        song_songTableView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openLinkEditor();
            }
        });

        Language language = Language.getInstance();

        TableColumn<SongData, Integer> id =
                new TableColumn<>(language.getString("scanner.song.table.songDataId"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<SongData, ?> song =
                new TableColumn<>(language.getString("scanner.song.table.song"));
        {
            TableColumn<SongData, LocalDlcSong> title =
                    new TableColumn<>(language.getString("scanner.song.table.title"));
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

            TableColumn<SongData, LocalDlcSong> composer =
                    new TableColumn<>(language.getString("scanner.song.table.composer"));
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
                new TableColumn<>(language.getString("scanner.song.table.linkedCaptures"));
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

        TableColumn<SongData, Boolean> select =
                new TableColumn<>(language.getString("common.select"));
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

    private void setupAnalysis() {
        setupAnalysis_analysisTableView();

        analysis_showButton.setOnAction(event -> openAnalysisDataViewer());

        analysis_startButton.setOnAction(event -> getView().scanner_analysis_startAnalysis());

        analysis_stopButton.setOnAction(event -> getView().scanner_analysis_stopAnalysis());

        analysis_clearButton.setOnAction(event -> getView().scanner_analysis_clearAnalysisData());
    }

    private void setupAnalysis_analysisTableView() {
        analysis_analysisTableView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                openAnalysisDataViewer();
            }
        });

        Language language = Language.getInstance();

        TableColumn<AnalysisData, Integer> id =
                new TableColumn<>(language.getString("scanner.analysis.table.analysisDataId"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<AnalysisData, ?> song =
                new TableColumn<>(language.getString("scanner.analysis.table.song"));
        {
            String songDataPropertyName = "songData";

            TableColumn<AnalysisData, SongData> title =
                    new TableColumn<>(language.getString("scanner.analysis.table.title"));
            title.setCellValueFactory(new PropertyValueFactory<>(songDataPropertyName));
            title.setPrefWidth(150);
            title.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(SongData item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.songProperty().get().title);
                }
            });
            title.setComparator(new Comparator<>() {
                private final TitleComparator titleComparator = new TitleComparator();

                @Override
                public int compare(SongData o1, SongData o2) {
                    return titleComparator.compare(o1.songProperty().get().title,
                            o2.songProperty().get().title);
                }
            });

            TableColumn<AnalysisData, SongData> composer =
                    new TableColumn<>(language.getString("scanner.analysis.table.composer"));
            composer.setCellValueFactory(new PropertyValueFactory<>(songDataPropertyName));
            composer.setPrefWidth(140);
            composer.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(SongData item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.songProperty().get().composer);
                }
            });

            TableColumn<AnalysisData, SongData> dlc = new TableColumn<>("DLC");
            dlc.setCellValueFactory(new PropertyValueFactory<>(songDataPropertyName));
            dlc.setPrefWidth(130);
            dlc.setCellFactory(param -> new TableCell<>() {
                @Override
                protected void updateItem(SongData item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item.songProperty().get().dlc);
                }
            });

            song.getColumns().addAll(List.of(title, composer, dlc));
        }

        TableColumn<AnalysisData, CaptureData> capture =
                new TableColumn<>(language.getString("scanner.analysis.table.capture"));
        capture.setCellValueFactory(new PropertyValueFactory<>("captureData"));
        capture.setPrefWidth(150);
        capture.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(CaptureData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String scannedTitle = item.scannedTitle.get();
                int id = item.idProperty().get();

                setText(String.format("(%d) %s", id, scannedTitle));
            }
        });

        TableColumn<AnalysisData, AnalysisData.Status> status =
                new TableColumn<>(language.getString("scanner.analysis.table.status"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setPrefWidth(100);
        status.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(AnalysisData.Status item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String key = switch (item) {
                    case ANALYZING -> "scanner.analysis.table.status.analyzing";
                    case CANCELED -> "scanner.analysis.table.status.canceled";
                    case DONE -> "scanner.analysis.table.status.done";
                    case ERROR -> "scanner.analysis.table.status.error";
                    case READY -> "scanner.analysis.table.status.ready";
                };

                setText(language.getString(key));
            }
        });

        TableColumn<AnalysisData, Exception> error =
                new TableColumn<>(language.getString("scanner.analysis.table.error"));
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

        analysis_analysisTableView.getColumns().addAll(List.of(id, song, capture, status, error));

        analysis_analysisTableView.setRowFactory(param -> new TableRow<>() {
            private static final String STYLE_CLASS_INVALID = "table-row-color-red";

            @Override
            protected void updateItem(AnalysisData item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll(STYLE_CLASS_INVALID);

                if (empty || item == null) {
                    return;
                }

                if (item.exception.get() != null) {
                    getStyleClass().add(STYLE_CLASS_INVALID);
                }
            }
        });
    }

    private void setupUploader() {
        setupUploader_recordTableView();

        uploader_refreshButton.setOnAction(event -> getView().scanner_uploader_refresh());

        uploader_selectAllButton.setOnAction(
                event -> uploader_recordTableView.getItems().forEach(x -> x.selected.set(true)));

        uploader_unselectAllButton.setOnAction(
                event -> uploader_recordTableView.getItems().forEach(x -> x.selected.set(false)));

        uploader_startUploadButton.setOnAction(event -> {
            long count = uploader_recordTableView.getItems().stream().filter(x -> x.selected.get())
                    .count();

            getView().scanner_uploader_startUpload(count);
        });

        uploader_stopUploadButton.setOnAction(event -> getView().scanner_uploader_stopUpload());
    }

    private void setupUploader_recordTableView() {
        Language language = Language.getInstance();

        TableColumn<NewRecordData, Integer> id =
                new TableColumn<>(language.getString("scanner.uploader.table.newRecordDataId"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setPrefWidth(100);

        TableColumn<NewRecordData, ?> song =
                new TableColumn<>(language.getString("scanner.uploader.table.song"));
        {
            String songPropertyName = "song";

            TableColumn<NewRecordData, LocalDlcSong> title =
                    new TableColumn<>(language.getString("scanner.uploader.table.title"));
            title.setCellValueFactory(new PropertyValueFactory<>(songPropertyName));
            title.setComparator(new DlcLocalSongComparator());
            title.setPrefWidth(140);
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

            TableColumn<NewRecordData, LocalDlcSong> composer =
                    new TableColumn<>(language.getString("scanner.uploader.table.composer"));
            composer.setCellValueFactory(new PropertyValueFactory<>(songPropertyName));
            composer.setPrefWidth(120);
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

            TableColumn<NewRecordData, LocalDlcSong> dlc = new TableColumn<>("DLC");
            dlc.setCellValueFactory(new PropertyValueFactory<>(songPropertyName));
            dlc.setPrefWidth(100);
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

        TableColumn<NewRecordData, Enums.Button> button =
                new TableColumn<>(language.getString("scanner.uploader.table.button"));
        button.setCellValueFactory(new PropertyValueFactory<>("button"));
        button.setPrefWidth(50);

        TableColumn<NewRecordData, Enums.Pattern> pattern =
                new TableColumn<>(language.getString("scanner.uploader.table.pattern"));
        pattern.setCellValueFactory(new PropertyValueFactory<>("pattern"));
        pattern.setPrefWidth(50);
        pattern.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Enums.Pattern item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                setText(item.getShortName());
            }
        });

        TableColumn<NewRecordData, ?> record =
                new TableColumn<>(language.getString("scanner.uploader.table.record"));
        {
            TableColumn<NewRecordData, LocalRecord> previousRecord =
                    new TableColumn<>(language.getString("scanner.uploader.table.record.previous"));
            previousRecord.setCellFactory(param -> new RecordTableCell());
            previousRecord.setCellValueFactory(new PropertyValueFactory<>("previousRecord"));
            previousRecord.setComparator((o1, o2) -> Float.compare(o2.rate, o1.rate));
            previousRecord.setPrefWidth(80);

            TableColumn<NewRecordData, LocalRecord> newRecord =
                    new TableColumn<>(language.getString("scanner.uploader.table.record.new"));
            newRecord.setCellFactory(param -> new RecordTableCell());
            newRecord.setCellValueFactory(new PropertyValueFactory<>("newRecord"));
            newRecord.setComparator((o1, o2) -> Float.compare(o2.rate, o1.rate));
            newRecord.setPrefWidth(80);

            record.getColumns().addAll(List.of(previousRecord, newRecord));
        }

        TableColumn<NewRecordData, Boolean> select =
                new TableColumn<>(language.getString("common.select"));
        select.setCellFactory(CheckBoxTableCell.forTableColumn(select));
        select.setCellValueFactory(new PropertyValueFactory<>("selected"));
        select.setPrefWidth(50);

        TableColumn<NewRecordData, NewRecordData.Status> status =
                new TableColumn<>(language.getString("scanner.uploader.table.status"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setPrefWidth(100);
        status.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(NewRecordData.Status item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                String key = switch (item) {
                    case CANCELED -> "scanner.uploader.table.status.canceled";
                    case HIGHER_RECORD_EXISTS -> "scanner.uploader.table.status.higherRecordExists";
                    case NONE -> null;
                    case READY -> "scanner.uploader.table.status.ready";
                    case UPLOADED -> "scanner.uploader.table.status.uploaded";
                    case UPLOADING -> "scanner.uploader.table.status.uploading";
                };

                setText(key == null ? "" : language.getString(key));
            }
        });

        uploader_recordTableView.getColumns()
                .addAll(List.of(id, song, button, pattern, record, select, status));
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
            getTransposeButton().setOnAction(event -> getGridPane().getChildren().forEach(x -> {
                int column = Objects.requireNonNullElse(GridPane.getColumnIndex(x), 0);
                int row = Objects.requireNonNullElse(GridPane.getRowIndex(x), 0);

                if (row != column) {
                    GridPane.setColumnIndex(x, row);
                    GridPane.setRowIndex(x, column);
                }
            }));
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


    public static class RecordTableCell extends TableCell<NewRecordData, LocalRecord> {
        private static final String STYLE_MAX_COMBO = "-fx-background-color: #C0FFC0;";
        private static final String STYLE_PERFECT = "-fx-background-color: #FFC0C0;";

        public RecordTableCell() {
            setAlignment(Pos.CENTER_RIGHT);
        }

        @Override
        protected void updateItem(LocalRecord item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setStyle(null);
                setText(null);
                return;
            }

            setText(String.format("%.2f", item.rate));

            if (item.rate == 100) { // NOPMD
                setStyle(STYLE_PERFECT);
            } else if (item.maxCombo) {
                setStyle(STYLE_MAX_COMBO);
            } else {
                setStyle(null);
            }
        }
    }
}
