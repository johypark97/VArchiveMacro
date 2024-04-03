package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ScannerFrontController;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.HomeStage;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private final NativeKeyListener nativeKeyListener;

    private WeakReference<ScannerComponent> scannerComponentReference;
    private WeakReference<ScannerSafeGlassComponent> scannerSafeGlassComponentReference;
    private WeakReference<ScannerDjNameInputComponent> scannerDjNameInputComponentReference;

    public HomeViewImpl() {
        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_END)) {
                    scanner_scanner_stop();
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
                        scanner_scanner_start();
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

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.initOwner(getStage());

        alert.setHeaderText(header);
        alert.setContentText(throwable.toString());

        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.initOwner(getStage());

        alert.setHeaderText(header);
        alert.setContentText(message);

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
    public void scanner_viewer_showSongTree(String filter) {
        getPresenter().scanner_viewer_onShowSongTree(getScanner().viewer_songTreeView, filter);
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
    public void scanner_scanner_setTabList(List<String> list) {
        getScanner().scanner_setTabList(list);
    }

    @Override
    public Set<String> scanner_scanner_getSelectedTabSet() {
        return getScanner().scanner_getSelectedTabSet();
    }

    @Override
    public void scanner_scanner_setSelectedTabSet(Set<String> value) {
        getScanner().scanner_setSelectedTabSet(value);
    }

    @Override
    public void scanner_scanner_start() {
        Set<String> selectedTabSet = getScanner().scanner_getSelectedTabSet();
        String cacheDirectory = getScanner().option_getCacheDirectory();
        int captureDelay = getScanner().option_getCaptureDelay();
        int keyInputDuration = getScanner().option_getKeyInputDuration();

        getPresenter().scanner_scanner_onStart(selectedTabSet, cacheDirectory, captureDelay,
                keyInputDuration);
    }

    @Override
    public void scanner_scanner_stop() {
        getPresenter().scanner_scanner_onStop();
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
    protected Stage newStage() {
        HomeStage stage = new HomeStage(this);

        scannerComponentReference = new WeakReference<>(stage.scannerComponent);
        scannerSafeGlassComponentReference = new WeakReference<>(stage.scannerSafeGlassComponent);
        scannerDjNameInputComponentReference =
                new WeakReference<>(stage.scannerDjNameInputComponent);

        stage.setOnShown(event -> {
            getPresenter().onViewShow_setupService();

            getPresenter().onViewShow_setupCacheDirectory(
                    getScanner().option_cacheDirectoryTextField);

            getPresenter().onViewShow_setupCaptureDelayLinker(
                    getScanner().optionCaptureDelayLinker);

            getPresenter().onViewShow_setupKeyInputDurationLinker(
                    getScanner().optionKeyInputDurationLinker);

            getPresenter().onViewShow_setupAccountFile(getScanner().option_accountFileTextField);

            getPresenter().onViewShow_setupRecordUploadDelayLinker(
                    getScanner().optionRecordUploadDelayLinker);

            if (!getPresenter().onViewShow_loadDatabase()) {
                return;
            }

            getPresenter().onViewShow_loadRecord();
        });

        stage.setOnHiding(event -> FxHookWrapper.removeKeyListener(nativeKeyListener));

        return stage;
    }

    private class ScannerFrontControllerImpl implements ScannerFrontController {
        @Override
        public void showForbiddenMark() {
            getScannerSafeGlass().showForbiddenMark();
            getScannerSafeGlass().setText("Failed to load database. Not available.");
            getScannerSafeGlass().setVisible(true);
            getScannerSafeGlass().requestFocus();
        }

        @Override
        public void showLoadingMark(String djName) {
            String message = String.format("Loading records from the server.\nDJ Name: %s", djName);

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
            scanner_viewer_showSongTree(null);

            getScanner().setVisible(true);
            getScanner().requestFocus();

            FxHookWrapper.addKeyListener(nativeKeyListener);
        }
    }
}
