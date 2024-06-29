package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.AnalysisDataManager.AnalysisData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.NewRecordDataManager.NewRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ScannerFrontController;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.MacroComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.HomeStage;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.awt.Toolkit;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private static final String GITHUB_URL = "https://github.com/johypark97/VArchiveMacro";

    private final NativeKeyListener scannerNativeKeyListener;
    private final NativeKeyListener macroNativeKeyListener;

    private WeakReference<ScannerComponent> scannerComponentReference;
    private WeakReference<ScannerSafeGlassComponent> scannerSafeGlassComponentReference;
    private WeakReference<ScannerDjNameInputComponent> scannerDjNameInputComponentReference;
    private WeakReference<MacroComponent> macroComponentReference;

    public HomeViewImpl() {
        scannerNativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_BACKSPACE)) {
                    scanner_capture_stop();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isCtrl() && !data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_ENTER)) {
                        scanner_capture_start();
                    }
                }
            }
        };

        macroNativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_BACKSPACE)) {
                    macro_stop();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (!data.isCtrl() && data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_OPEN_BRACKET)) {
                        macro_start_up();
                    } else if (data.isPressed(NativeKeyEvent.VC_CLOSE_BRACKET)) {
                        macro_start_down();
                    }
                }
            }
        };
    }

    private ScannerComponent getScanner() {
        return scannerComponentReference.get();
    }

    private ScannerSafeGlassComponent getScannerSafeGlass() {
        return scannerSafeGlassComponentReference.get();
    }

    private ScannerDjNameInputComponent getScannerDjNameInput() {
        return scannerDjNameInputComponentReference.get();
    }

    private MacroComponent getMacro() {
        return macroComponentReference.get();
    }

    @Override
    public void requestStop() {
        getPresenter().stopPresenter();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(getStage()).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(getStage()).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(getStage()).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void home_changeLanguage(Locale locale) {
        getPresenter().home_onChangeLanguage(locale);
    }

    @Override
    public void home_openOpenSourceLicense() {
        getPresenter().home_onOpenOpenSourceLicense(getStage());
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

        Alert alert = AlertBuilder.information().setOwner(getStage()).alert;
        alert.getDialogPane().setContent(box);

        alert.showAndWait();
    }

    @Override
    public ScannerFrontController getScannerFrontController() {
        return new ScannerFrontControllerImpl();
    }

    @Override
    public void scanner_front_loadRemoteRecord(String djName) {
        getPresenter().scanner_front_onLoadRemoteRecord(djName);
    }

    @Override
    public void scanner_viewer_setSongTreeViewRoot(TreeItem<ViewerTreeData> root) {
        getScanner().viewer_setSongTreeViewRoot(root);
    }

    @Override
    public void scanner_viewer_updateSongTreeViewFilter(String filter) {
        getPresenter().scanner_viewer_onUpdateSongTreeViewFilter(filter);
    }

    @Override
    public void scanner_viewer_showRecord(int id) {
        ViewerRecordData data = getPresenter().scanner_viewer_onShowRecord(id);

        getScanner().viewer_showInformation(data.title, data.composer);

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                getScanner().viewer_resetRecord(i, j);

                float rate = data.rate[i][j];
                if (rate == -1) {
                    getScanner().viewer_shadowRecord(i, j);
                } else {
                    boolean maxCombo = data.maxCombo[i][j];
                    getScanner().viewer_setRecord(i, j, rate, maxCombo);
                }
            }
        }
    }

    @Override
    public void scanner_capture_setCaptureDataList(ObservableList<CaptureData> list) {
        getScanner().capture_setCaptureDataList(list);
    }

    @Override
    public void scanner_capture_openCaptureViewer(int id) {
        String cacheDirectory = getScanner().option_getCacheDirectory();
        getPresenter().scanner_capture_onOpenCaptureViewer(getStage(), cacheDirectory, id);
    }

    @Override
    public void scanner_capture_clearScanData() {
        getPresenter().scanner_capture_onClearScanData();
    }

    @Override
    public void scanner_capture_refresh() {
        getScanner().capture_refresh();
    }

    @Override
    public void scanner_capture_setTabList(List<String> list) {
        getScanner().capture_setTabList(list);
    }

    @Override
    public Set<String> scanner_capture_getSelectedTabSet() {
        return getScanner().capture_getSelectedTabSet();
    }

    @Override
    public void scanner_capture_setSelectedTabSet(Set<String> value) {
        getScanner().capture_setSelectedTabSet(value);
    }

    @Override
    public void scanner_capture_start() {
        Set<String> selectedTabSet = getScanner().capture_getSelectedTabSet();
        String cacheDirectory = getScanner().option_getCacheDirectory();
        int captureDelay = getScanner().option_getCaptureDelay();
        int keyInputDuration = getScanner().option_getKeyInputDuration();

        getPresenter().scanner_capture_onStart(selectedTabSet, cacheDirectory, captureDelay,
                keyInputDuration);
    }

    @Override
    public void scanner_capture_stop() {
        getPresenter().scanner_capture_onStop();
    }

    @Override
    public void scanner_song_setSongDataList(ObservableList<SongData> list) {
        getScanner().song_setSongDataList(list);
    }

    @Override
    public void scanner_song_openLinkEditor(int id) {
        String cacheDirectory = getScanner().option_getCacheDirectory();
        getPresenter().scanner_song_onOpenLinkEditor(getStage(), cacheDirectory, id);
    }

    @Override
    public void scanner_song_refresh() {
        getScanner().song_refresh();
    }

    @Override
    public void scanner_analysis_setAnalysisDataList(ObservableList<AnalysisData> list) {
        getScanner().setAnalysisDataList(list);
    }

    @Override
    public void scanner_analysis_clearAnalysisData() {
        getPresenter().scanner_analysis_onClearAnalysisData();
    }

    @Override
    public void scanner_analysis_openAnalysisDataViewer(int id) {
        String cacheDirectory = getScanner().option_getCacheDirectory();
        getPresenter().scanner_analysis_onOpenAnalysisDataViewer(getStage(), cacheDirectory, id);
    }

    @Override
    public void scanner_analysis_startAnalysis() {
        String cacheDirectory = getScanner().option_getCacheDirectory();
        getPresenter().scanner_analysis_onStartAnalysis(cacheDirectory);
    }

    @Override
    public void scanner_analysis_stopAnalysis() {
        getPresenter().scanner_analysis_onStopAnalysis();
    }

    @Override
    public void scanner_uploader_setNewRecordDataList(ObservableList<NewRecordData> list) {
        getScanner().setNewRecordDataList(list);
    }

    @Override
    public void scanner_uploader_refresh() {
        getPresenter().scanner_uploader_onRefresh();
    }

    @Override
    public void scanner_uploader_startUpload(long count) {
        getPresenter().scanner_uploader_onStartUpload(count);
    }

    @Override
    public void scanner_uploader_stopUpload() {
        getPresenter().scanner_uploader_onStopUpload();
    }

    @Override
    public String scanner_option_getCacheDirectory() {
        return getScanner().option_getCacheDirectory();
    }

    @Override
    public int scanner_option_getCaptureDelay() {
        return getScanner().option_getCaptureDelay();
    }

    @Override
    public int scanner_option_getKeyInputDuration() {
        return getScanner().option_getKeyInputDuration();
    }

    @Override
    public String scanner_option_getAccountFile() {
        return getScanner().option_getAccountFile();
    }

    @Override
    public int scanner_option_getRecordUploadDelay() {
        return getScanner().option_getRecordUploadDelay();
    }

    @Override
    public void scanner_option_openCacheDirectorySelector() {
        Path path = getPresenter().scanner_option_onOpenCacheDirectorySelector(getStage());
        if (path != null) {
            getScanner().option_setCacheDirectory(path.toString());
        }
    }

    @Override
    public void scanner_option_openAccountFileSelector() {
        Path path = getPresenter().scanner_option_onOpenAccountFileSelector(getStage());
        if (path != null) {
            getScanner().option_setAccountFile(path.toString());
        }
    }

    @Override
    public AnalysisKey macro_getAnalysisKey() {
        return getMacro().getAnalysisKey();
    }

    @Override
    public void macro_setAnalysisKey(AnalysisKey key) {
        getMacro().setAnalysisKey(key);
    }

    @Override
    public int macro_getCount() {
        return getMacro().countLinker.getValue();
    }

    @Override
    public int macro_getCaptureDelay() {
        return getMacro().captureDelayLinker.getValue();
    }

    @Override
    public int macro_getCaptureDuration() {
        return getMacro().captureDurationLinker.getValue();
    }

    @Override
    public int macro_getKeyInputDuration() {
        return getMacro().keyInputDurationLinker.getValue();
    }

    @Override
    public void macro_start_up() {
        getPresenter().macro_onStart_up();
    }

    @Override
    public void macro_start_down() {
        getPresenter().macro_onStart_down();
    }

    @Override
    public void macro_stop() {
        getPresenter().macro_onStop();
    }

    @Override
    protected Stage newStage() {
        HomeStage stage = new HomeStage(this);

        scannerComponentReference = new WeakReference<>(stage.scannerComponent);
        scannerSafeGlassComponentReference = new WeakReference<>(stage.scannerSafeGlassComponent);
        scannerDjNameInputComponentReference =
                new WeakReference<>(stage.scannerDjNameInputComponent);
        macroComponentReference = new WeakReference<>(stage.macroComponent);

        stage.setOnShown(event -> {
            getPresenter().onViewShow_setupService();

            getPresenter().onViewShow_scanner_setupCacheDirectory(
                    getScanner().option_cacheDirectoryTextField);

            getPresenter().onViewShow_scanner_setupCaptureDelayLinker(
                    getScanner().optionCaptureDelayLinkerInitializer());

            getPresenter().onViewShow_scanner_setupKeyInputDurationLinker(
                    getScanner().optionKeyInputDurationLinkerInitializer());

            getPresenter().onViewShow_scanner_setupAccountFile(
                    getScanner().option_accountFileTextField);

            getPresenter().onViewShow_scanner_setupRecordUploadDelayLinker(
                    getScanner().optionRecordUploadDelayLinkerInitializer());

            getPresenter().onViewShow_macro_setupAnalysisKey();

            getPresenter().onViewShow_macro_setupCountLinker(getMacro().countLinker);

            getPresenter().onViewShow_macro_setupCaptureDelayLinker(getMacro().captureDelayLinker);

            getPresenter().onViewShow_macro_setupCaptureDurationLinker(
                    getMacro().captureDurationLinker);

            getPresenter().onViewShow_macro_setupKeyInputDurationLinker(
                    getMacro().keyInputDurationLinker);

            if (!getPresenter().onViewShow_loadDatabase()) {
                return;
            }

            getPresenter().onViewShow_loadRecord();

            FxHookWrapper.addKeyListener(macroNativeKeyListener);
        });

        stage.setOnHiding(event -> {
            FxHookWrapper.removeKeyListener(scannerNativeKeyListener);
            FxHookWrapper.removeKeyListener(macroNativeKeyListener);
        });

        return stage;
    }

    private class ScannerFrontControllerImpl implements ScannerFrontController {
        @Override
        public void showForbiddenMark() {
            String message = Language.getInstance().getString("scannerSafeGlass.forbiddenMark");

            getScannerSafeGlass().showForbiddenMark();
            getScannerSafeGlass().setText(message);
            getScannerSafeGlass().setVisible(true);
            getScannerSafeGlass().requestFocus();
        }

        @Override
        public void showLoadingMark(String djName) {
            String message =
                    Language.getInstance().getFormatString("scannerSafeGlass.loadingMark", djName);

            getScannerDjNameInput().upEffect();
            getScannerSafeGlass().showLoadingMark();
            getScannerSafeGlass().setText(message);
            getScannerSafeGlass().setVisible(true);
            getScannerSafeGlass().requestFocus();
        }

        @Override
        public void hideLoadingMark() {
            getScannerSafeGlass().setVisible(false);
            getScannerDjNameInput().downEffect();
        }

        @Override
        public void showDjNameInput() {
            getScannerDjNameInput().setVisible(true);
            getScannerDjNameInput().requestFocus();
        }

        @Override
        public void hideDjNameInput() {
            getScannerDjNameInput().setVisible(false);
        }

        @Override
        public void showDjNameInputError(String message) {
            getScannerDjNameInput().showError(message);
        }

        @Override
        public void hideDjNameInputError() {
            getScannerDjNameInput().hideError();
        }

        @Override
        public void showScanner() {
            ((HomeStage) getStage()).replaceScannerTabContent();
            getScanner().requestFocus();

            FxHookWrapper.addKeyListener(scannerNativeKeyListener);
        }
    }
}
