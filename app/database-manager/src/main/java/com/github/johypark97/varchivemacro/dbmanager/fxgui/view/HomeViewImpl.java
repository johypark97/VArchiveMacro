package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.core.NativeKeyEventData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.lib.common.HookWrapper;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpView;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private static final String TITLE = "Database Manager";

    private static final String GLOBAL_CSS_FILENAME = "global.css";
    private static final String TABLE_COLOR_CSS_FILENAME = "table-color.css";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final NativeKeyListener nativeKeyListener;

    private final HomeComponent homeComponent = new HomeComponent(this);

    public HomeViewImpl() {
        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);
                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_END)) {
                    ocrCacheCapturer_stop();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);
                if (data.isOtherMod()) {
                    return;
                }

                if (data.isCtrl() && !data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_HOME)) {
                        ocrCacheCapturer_start();
                    }
                }
            }
        };
    }

    @Override
    public void viewer_updateTableFilter() {
        String regex = homeComponent.viewer_filterTextField.getText();
        SongDataProperty property = homeComponent.viewer_filterComboBox.getValue();

        getPresenter().viewer_onUpdateTableFilter(regex, property);
    }

    @Override
    public void checker_setResultText(String value) {
        homeComponent.checker_textArea.setText(value);
    }

    @Override
    public void checker_validateDatabase() {
        getPresenter().checker_onValidateDatabase();
    }

    @Override
    public void checker_compareDatabaseWithRemote() {
        getPresenter().checker_onCompareDatabaseWithRemote();
    }

    @Override
    public void ocrTester_selectCacheDirectory() {
        Path path = getPresenter().ocrTester_onSelectCacheDirectory(getStage());
        if (path != null) {
            homeComponent.ocrTester_cacheDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrTester_selectTessdataDirectory() {
        Path path = getPresenter().ocrTester_onSelectTessdataDirectory(getStage());
        if (path != null) {
            homeComponent.ocrTester_tessdataDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrTester_start() {
        String cacheDirectory = homeComponent.ocrTester_cacheDirectoryTextField.getText();
        String tessdataDirectory = homeComponent.ocrTester_tessdataDirectoryTextField.getText();
        String tessdataLanguage = homeComponent.ocrTester_tessdataLanguageTextField.getText();

        getPresenter().ocrTester_onStart(cacheDirectory, tessdataDirectory, tessdataLanguage);
    }

    @Override
    public void ocrTester_stop() {
        getPresenter().ocrTester_onStop();
    }

    @Override
    public void ocrTester_updateProgressIndicator(double value) {
        homeComponent.ocrTester_progressBar.setProgress(value);
        homeComponent.ocrTester_progressLabel.setText(
                (value >= 0 && value <= 1) ? String.format("%.2f%%", value * 100) : "");
    }

    @Override
    public void ocrCacheCapturer_selectOutputDirectory() {
        Path path = getPresenter().ocrCacheCapturer_onSelectOutputDirectory(getStage());
        if (path != null) {
            homeComponent.ocrCacheCapturer_outputDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrCacheCapturer_start() {
        int captureDelay = homeComponent.ocrCacheCapturer_captureDelayLinker.getValue();
        int keyInputDelay = homeComponent.ocrCacheCapturer_keyInputDelayLinker.getValue();
        int keyInputDuration = homeComponent.ocrCacheCapturer_keyInputDurationLinker.getValue();
        String outputDirectory = homeComponent.ocrCacheCapturer_outputDirectoryTextField.getText();

        getPresenter().ocrCacheCapturer_onStart(captureDelay, keyInputDelay, keyInputDuration,
                outputDirectory);
    }

    @Override
    public void ocrCacheCapturer_stop() {
        getPresenter().ocrCacheCapturer_onStop();
    }

    @Override
    protected HomeView getInstance() {
        return this;
    }

    @Override
    protected Stage newStage() {
        URL globalCss = HomeViewImpl.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        URL tableColorCss = HomeViewImpl.class.getResource(TABLE_COLOR_CSS_FILENAME);
        Objects.requireNonNull(tableColorCss);

        Scene scene = new Scene(homeComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setTitle(TITLE);

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);

        stage.setOnShowing(event -> {
            getPresenter().onViewShowing_viewer_linkTableView(homeComponent.viewer_tableView);
            getPresenter().onViewShowing_viewer_setFilterColumn(
                    homeComponent.viewer_filterComboBox);

            getPresenter().onViewShowing_ocrTester_linkTableView(homeComponent.ocrTester_tableView);

            getPresenter().onViewShowing_ocrCacheCapturer_setupCaptureDelayLinker(
                    homeComponent.ocrCacheCapturer_captureDelayLinker);
            getPresenter().onViewShowing_ocrCacheCapturer_setupKeyInputDelayLinker(
                    homeComponent.ocrCacheCapturer_keyInputDelayLinker);
            getPresenter().onViewShowing_ocrCacheCapturer_setupKeyInputDurationLinker(
                    homeComponent.ocrCacheCapturer_keyInputDurationLinker);

            HookWrapper.addKeyListener(nativeKeyListener);
        });

        stage.setOnHiding(event -> HookWrapper.removeKeyListener(nativeKeyListener));

        return stage;
    }

    @Override
    protected boolean onStartView() {
        return getPresenter().initialize();
    }

    @Override
    protected boolean onStopView() {
        return getPresenter().terminate();
    }
}
