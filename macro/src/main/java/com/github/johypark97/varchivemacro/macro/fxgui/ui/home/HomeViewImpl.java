package com.github.johypark97.varchivemacro.macro.fxgui.ui.home;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.GlobalResource;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.ScannerFrontController;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HomeViewImpl extends BorderPane implements HomeView {
    private static final String FXML_PATH = "/fxml/home/Home.fxml";
    private static final String GITHUB_URL = "https://github.com/johypark97/VArchiveMacro";

    private final MacroComponent macroComponent = new MacroComponent();
    private final ScannerComponent scannerComponent = new ScannerComponent(this);
    private final ScannerDjNameInputComponent scannerDjNameInputComponent =
            new ScannerDjNameInputComponent(this);
    private final ScannerSafeGlassComponent scannerSafeGlassComponent =
            new ScannerSafeGlassComponent();

    private final Stage stage;

    @MvpPresenter
    public HomePresenter presenter;

    @FXML
    public MenuItem exitMenuItem;

    @FXML
    public RadioMenuItem langEnRadioMenuItem;

    @FXML
    public RadioMenuItem langKoRadioMenuItem;

    @FXML
    public MenuItem openSourceLicenseMenuItem;

    @FXML
    public MenuItem aboutMenuItem;

    @FXML
    public Tab scannerTab;

    @FXML
    public Tab macroTab;

    public HomeViewImpl(Stage stage) {
        this.stage = stage;

        URL fxmlUrl = HomeViewImpl.class.getResource(FXML_PATH);
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        try {
            Mvp.loadFxml(this, fxmlUrl,
                    x -> x.setResources(Language.getInstance().getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(this);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        stage.setScene(scene);

        stage.setOnShown(event -> presenter.onStartView());
        Mvp.hookWindowCloseRequest(stage, event -> presenter.onStopView());
    }

    @FXML
    public void initialize() {
        scannerTab.setContent(
                new StackPane(scannerDjNameInputComponent, scannerSafeGlassComponent));
        macroTab.setContent(macroComponent);

        Locale locale = Language.getInstance().getLocale();
        if (Locale.ENGLISH.equals(locale)) {
            langEnRadioMenuItem.setSelected(true);
        } else if (Locale.KOREAN.equals(locale)) {
            langKoRadioMenuItem.setSelected(true);
        }

        exitMenuItem.setOnAction(event -> presenter.onStopView());

        langEnRadioMenuItem.setOnAction(event -> presenter.home_changeLanguage(Locale.ENGLISH));
        langKoRadioMenuItem.setOnAction(event -> presenter.home_changeLanguage(Locale.KOREAN));

        openSourceLicenseMenuItem.setOnAction(event -> presenter.home_openOpenSourceLicense());
        aboutMenuItem.setOnAction(event -> presenter.home_openAbout());
    }

    @Override
    public Window getWindow() {
        return stage;
    }

    @Override
    public void startView() {
        stage.show();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void home_openAbout() {
        VBox box = new VBox();
        box.setPadding(new Insets(20));
        box.setSpacing(10);
        {
            box.getChildren().add(new Label("Version: " + BuildInfo.version));
            box.getChildren().add(new Label("Build date: " + BuildInfo.date));

            HBox sourceCodeBox = new HBox();
            sourceCodeBox.setAlignment(Pos.CENTER_LEFT);
            sourceCodeBox.setSpacing(10);
            {
                sourceCodeBox.getChildren().add(new Label("Source code: "));

                TextField textField = new TextField(GITHUB_URL);
                textField.setEditable(false);
                HBox.setHgrow(textField, Priority.ALWAYS);
                sourceCodeBox.getChildren().add(textField);
            }
            box.getChildren().add(sourceCodeBox);
        }

        Alert alert = AlertBuilder.information().setOwner(stage).alert;
        alert.getDialogPane().setContent(box);

        alert.showAndWait();
    }

    @Override
    public ScannerFrontController getScannerFrontController() {
        return new ScannerFrontControllerImpl();
    }

    @Override
    public void scanner_viewer_setSongTreeViewRoot(TreeItem<ViewerTreeData> root) {
        scannerComponent.viewer_setSongTreeViewRoot(root);
    }

    @Override
    public void scanner_viewer_setSongInformationText(String value) {
        scannerComponent.viewer_setSongInformationText(value);
    }

    @Override
    public void scanner_viewer_setRecordData(ViewerRecordData data) {
        scannerComponent.viewer_setRecordData(data);
    }

    @Override
    public void scanner_capture_setCaptureDataList(ObservableList<CaptureData> list) {
        scannerComponent.capture_setCaptureDataList(list);
    }

    @Override
    public void scanner_capture_refresh() {
        scannerComponent.capture_refresh();
    }

    @Override
    public void scanner_capture_setTabList(List<String> list) {
        scannerComponent.capture_setTabList(list);
    }

    @Override
    public Set<String> scanner_capture_getSelectedCategorySet() {
        return scannerComponent.capture_getSelectedCategorySet();
    }

    @Override
    public void scanner_capture_setSelectedCategorySet(Set<String> value) {
        scannerComponent.capture_setSelectedCategorySet(value);
    }

    @Override
    public void scanner_song_setSongDataList(ObservableList<SongData> list) {
        scannerComponent.song_setSongDataList(list);
    }

    @Override
    public void scanner_song_refresh() {
        scannerComponent.song_refresh();
    }

    @Override
    public void scanner_analysis_setAnalysisDataList(ObservableList<AnalysisData> list) {
        scannerComponent.analysis_setAnalysisDataList(list);
    }

    @Override
    public void scanner_analysis_setProgressBarValue(double value) {
        scannerComponent.setAnalysis_progressBarValue(value);
    }

    @Override
    public void scanner_analysis_setProgressLabelText(String value) {
        scannerComponent.setAnalysis_progressLabelText(value);
    }

    @Override
    public void scanner_uploader_setNewRecordDataList(ObservableList<NewRecordData> list) {
        scannerComponent.uploader_setNewRecordDataList(list);
    }

    @Override
    public String scanner_option_getCacheDirectory() {
        return scannerComponent.option_getCacheDirectory();
    }

    @Override
    public void scanner_option_setCacheDirectory(String value) {
        scannerComponent.option_setCacheDirectory(value);
    }

    @Override
    public void scanner_option_setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        scannerComponent.option_setupCaptureDelaySlider(defaultValue, limitMax, limitMin, value);
    }

    @Override
    public int scanner_option_getCaptureDelay() {
        return scannerComponent.option_getCaptureDelay();
    }

    @Override
    public void scanner_option_setupKeyInputDurationSlider(int defaultValue, int limitMax,
            int limitMin, int value) {
        scannerComponent.option_setupKeyInputDurationSlider(defaultValue, limitMax, limitMin,
                value);
    }

    @Override
    public int scanner_option_getKeyInputDuration() {
        return scannerComponent.option_getKeyInputDuration();
    }

    @Override
    public void scanner_option_setupAnalysisThreadCountSlider(int defaultValue, int max,
            int value) {
        scannerComponent.option_setupAnalysisThreadCountSlider(defaultValue, max, value);
    }

    @Override
    public int scanner_option_getupAnalysisThreadCount() {
        return scannerComponent.option_getAnalysisThreadCount();
    }

    @Override
    public String scanner_option_getAccountFile() {
        return scannerComponent.option_getAccountFile();
    }

    @Override
    public void scanner_option_setAccountFile(String value) {
        scannerComponent.option_setAccountFile(value);
    }

    @Override
    public void scanner_option_setupRecordUploadDelaySlider(int defaultValue, int limitMax,
            int limitMin, int value) {
        scannerComponent.option_setupRecordUploadDelaySlider(defaultValue, limitMax, limitMin,
                value);
    }

    @Override
    public int scanner_option_getRecordUploadDelay() {
        return scannerComponent.option_getRecordUploadDelay();
    }

    @Override
    public File scanner_option_openCacheDirectorySelector(Path initialDirectory) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(initialDirectory.toFile());
        chooser.setTitle(Language.getInstance()
                .getString("scanner.option.dialog.cacheDirectorySelectorTitle"));

        return chooser.showDialog(stage);
    }

    @Override
    public File scanner_option_openAccountFileSelector(Path initialDirectory) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(initialDirectory.toFile());
        chooser.setTitle(
                Language.getInstance().getString("scanner.option.dialog.AccountFileSelectorTitle"));

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account text file (*.txt)", "*.txt"));

        return chooser.showOpenDialog(stage);
    }

    @Override
    public AnalysisKey macro_getAnalysisKey() {
        return macroComponent.getAnalysisKey();
    }

    @Override
    public void macro_setAnalysisKey(AnalysisKey key) {
        macroComponent.setAnalysisKey(key);
    }

    @Override
    public void macro_setupCountSlider(int defaultValue, int limitMax, int limitMin, int value) {
        macroComponent.setupCountSlider(defaultValue, limitMax, limitMin, value);
    }

    @Override
    public int macro_getCount() {
        return macroComponent.getCount();
    }

    @Override
    public void macro_setupCaptureDelaySlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        macroComponent.setupCaptureDelaySlider(defaultValue, limitMax, limitMin, value);
    }

    @Override
    public int macro_getCaptureDelay() {
        return macroComponent.getCaptureDelay();
    }

    @Override
    public void macro_setupCaptureDurationSlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        macroComponent.setupCaptureDurationSlider(defaultValue, limitMax, limitMin, value);
    }

    @Override
    public int macro_getCaptureDuration() {
        return macroComponent.getCaptureDuration();
    }

    @Override
    public void macro_setupKeyInputDurationSlider(int defaultValue, int limitMax, int limitMin,
            int value) {
        macroComponent.setupKeyInputDurationLinkerSlider(defaultValue, limitMax, limitMin, value);
    }

    @Override
    public int macro_getKeyInputDuration() {
        return macroComponent.getKeyInputDuration();
    }

    private class ScannerFrontControllerImpl implements ScannerFrontController {
        @Override
        public void showForbiddenMark() {
            String message = Language.getInstance().getString("scannerSafeGlass.forbiddenMark");

            scannerSafeGlassComponent.showForbiddenMark();
            scannerSafeGlassComponent.setText(message);
            scannerSafeGlassComponent.setVisible(true);
            scannerSafeGlassComponent.requestFocus();
        }

        @Override
        public void showLoadingMark(String djName) {
            String message =
                    Language.getInstance().getFormatString("scannerSafeGlass.loadingMark", djName);

            scannerDjNameInputComponent.upEffect();
            scannerSafeGlassComponent.showLoadingMark();
            scannerSafeGlassComponent.setText(message);
            scannerSafeGlassComponent.setVisible(true);
            scannerSafeGlassComponent.requestFocus();
        }

        @Override
        public void hideLoadingMark() {
            scannerSafeGlassComponent.setVisible(false);
            scannerDjNameInputComponent.downEffect();
        }

        @Override
        public void showDjNameInput() {
            scannerDjNameInputComponent.setVisible(true);
            scannerDjNameInputComponent.requestFocus();
        }

        @Override
        public void hideDjNameInput() {
            scannerDjNameInputComponent.setVisible(false);
        }

        @Override
        public void showDjNameInputError(String message) {
            scannerDjNameInputComponent.showError(message);
        }

        @Override
        public void hideDjNameInputError() {
            scannerDjNameInputComponent.hideError();
        }

        @Override
        public void showScanner() {
            scannerTab.setContent(scannerComponent);

            scannerComponent.requestFocus();
        }
    }
}
