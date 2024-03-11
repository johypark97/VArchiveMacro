package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.stage.HomeStage;
import com.github.johypark97.varchivemacro.lib.hook.HookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import javafx.stage.Stage;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private final NativeKeyListener nativeKeyListener;

    private WeakReference<HomeComponent> homeComponentReference;

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

    private HomeComponent getHomeComponent() {
        return homeComponentReference.get();
    }

    @Override
    public void viewer_updateTableFilter() {
        String regex = getHomeComponent().viewer_filterTextField.getText();
        SongDataProperty property = getHomeComponent().viewer_filterComboBox.getValue();

        getPresenter().viewer_onUpdateTableFilter(regex, property);
    }

    @Override
    public void checker_setResultText(String value) {
        getHomeComponent().checker_textArea.setText(value);
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
            getHomeComponent().ocrTester_cacheDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrTester_selectTessdataDirectory() {
        Path path = getPresenter().ocrTester_onSelectTessdataDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrTester_tessdataDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrTester_start() {
        String cacheDirectory = getHomeComponent().ocrTester_cacheDirectoryTextField.getText();
        String tessdataDirectory =
                getHomeComponent().ocrTester_tessdataDirectoryTextField.getText();
        String tessdataLanguage = getHomeComponent().ocrTester_tessdataLanguageTextField.getText();

        getPresenter().ocrTester_onStart(cacheDirectory, tessdataDirectory, tessdataLanguage);
    }

    @Override
    public void ocrTester_stop() {
        getPresenter().ocrTester_onStop();
    }

    @Override
    public void ocrTester_updateProgressIndicator(double value) {
        getHomeComponent().ocrTester_progressBar.setProgress(value);
        getHomeComponent().ocrTester_progressLabel.setText(
                (value >= 0 && value <= 1) ? String.format("%.2f%%", value * 100) : "");
    }

    @Override
    public void ocrCacheCapturer_selectOutputDirectory() {
        Path path = getPresenter().ocrCacheCapturer_onSelectOutputDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrCacheCapturer_outputDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrCacheCapturer_start() {
        int captureDelay = getHomeComponent().ocrCacheCapturer_captureDelayLinker.getValue();
        int keyInputDelay = getHomeComponent().ocrCacheCapturer_keyInputDelayLinker.getValue();
        int keyInputDuration =
                getHomeComponent().ocrCacheCapturer_keyInputDurationLinker.getValue();
        String outputDirectory =
                getHomeComponent().ocrCacheCapturer_outputDirectoryTextField.getText();

        getPresenter().ocrCacheCapturer_onStart(captureDelay, keyInputDelay, keyInputDuration,
                outputDirectory);
    }

    @Override
    public void ocrCacheCapturer_stop() {
        getPresenter().ocrCacheCapturer_onStop();
    }

    @Override
    public void ocrCacheClassifier_selectInputDirectory() {
        Path path = getPresenter().ocrCacheClassifier_onSelectInputDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrCacheClassifier_inputDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrCacheClassifier_selectOutputDirectory() {
        Path path = getPresenter().ocrCacheClassifier_onSelectOutputDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrCacheClassifier_outputDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void ocrCacheClassifier_updateProgressIndicator(double value) {
        getHomeComponent().ocrCacheClassifier_progressBar.setProgress(value);
        getHomeComponent().ocrCacheClassifier_progressLabel.setText(
                (value >= 0 && value <= 1) ? String.format("%.2f%%", value * 100) : "");
    }

    @Override
    public void ocrCacheClassifier_start() {
        String inputDirectory =
                getHomeComponent().ocrCacheClassifier_inputDirectoryTextField.getText();
        String outputDirectory =
                getHomeComponent().ocrCacheClassifier_outputDirectoryTextField.getText();

        getPresenter().ocrCacheClassifier_onStart(inputDirectory, outputDirectory);
    }

    @Override
    public void ocrCacheClassifier_stop() {
        getPresenter().ocrCacheClassifier_onStop();
    }

    @Override
    public void ocrGroundTruthGenerator_selectInputDirectory() {
        Path path = getPresenter().ocrGroundTruthGenerator_onSelectInputDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrGroundTruthGenerator_inputDirectoryTextField.setText(
                    path.toString());
        }
    }

    @Override
    public void ocrGroundTruthGenerator_selectOutputDirectory() {
        Path path = getPresenter().ocrGroundTruthGenerator_onSelectOutputDirectory(getStage());
        if (path != null) {
            getHomeComponent().ocrGroundTruthGenerator_outputDirectoryTextField.setText(
                    path.toString());
        }
    }

    @Override
    public void ocrGroundTruthGenerator_updateProgressIndicator(double value) {
        getHomeComponent().ocrGroundTruthGenerator_progressBar.setProgress(value);
        getHomeComponent().ocrGroundTruthGenerator_progressLabel.setText(
                (value >= 0 && value <= 1) ? String.format("%.2f%%", value * 100) : "");
    }

    @Override
    public void ocrGroundTruthGenerator_start() {
        String inputDirectory =
                getHomeComponent().ocrGroundTruthGenerator_inputDirectoryTextField.getText();
        String outputDirectory =
                getHomeComponent().ocrGroundTruthGenerator_outputDirectoryTextField.getText();

        getPresenter().ocrGroundTruthGenerator_onStart(inputDirectory, outputDirectory);
    }

    @Override
    public void ocrGroundTruthGenerator_stop() {
        getPresenter().ocrGroundTruthGenerator_onStop();
    }

    @Override
    public void liveTester_selectTessdataDirectory() {
        Path path = getPresenter().liveTester_onSelectTessdataDirectory(getStage());
        if (path != null) {
            getHomeComponent().liveTester_tessdataDirectoryTextField.setText(path.toString());
        }
    }

    @Override
    public void liveTester_open() {
        String tessdataDirectory =
                getHomeComponent().liveTester_tessdataDirectoryTextField.getText();
        String tessdataLanguage = getHomeComponent().liveTester_tessdataLanguageTextField.getText();

        getPresenter().liveTester_onOpen(tessdataDirectory, tessdataLanguage);
    }

    @Override
    public void liveTester_close() {
        getPresenter().liveTester_onClose();
    }

    @Override
    protected Stage newStage() {
        HomeStage stage = new HomeStage(this);

        homeComponentReference = new WeakReference<>(stage.homeComponent);

        stage.setOnShowing(event -> {
            getPresenter().onViewShowing_viewer_linkTableView(getHomeComponent().viewer_tableView);
            getPresenter().onViewShowing_viewer_setFilterColumn(
                    getHomeComponent().viewer_filterComboBox);

            getPresenter().onViewShowing_ocrTester_linkTableView(
                    getHomeComponent().ocrTester_tableView);

            getPresenter().onViewShowing_ocrCacheCapturer_setupCaptureDelayLinker(
                    getHomeComponent().ocrCacheCapturer_captureDelayLinker);
            getPresenter().onViewShowing_ocrCacheCapturer_setupKeyInputDelayLinker(
                    getHomeComponent().ocrCacheCapturer_keyInputDelayLinker);
            getPresenter().onViewShowing_ocrCacheCapturer_setupKeyInputDurationLinker(
                    getHomeComponent().ocrCacheCapturer_keyInputDurationLinker);

            HookWrapper.addKeyListener(nativeKeyListener);
        });

        stage.setOnHiding(event -> HookWrapper.removeKeyListener(nativeKeyListener));

        return stage;
    }
}
