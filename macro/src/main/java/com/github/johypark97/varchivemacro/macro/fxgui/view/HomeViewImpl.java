package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ScannerFrontController;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.HomeStage;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private WeakReference<ScannerComponent> scannerComponentReference;
    private WeakReference<ScannerSafeGlassComponent> scannerSafeGlassComponentReference;
    private WeakReference<ScannerDjNameInputComponent> scannerDjNameInputComponentReference;

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
    public void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(getStage());

        alert.setHeaderText(message);
        alert.setContentText(e.toString());

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
        getPresenter().scanner_viewer_onShowSongTree(getScanner().viewer_treeView, filter);
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
    public void scanner_scanner_setDlcList(List<String> list) {
        getScanner().scanner_setDlcList(list);
    }

    @Override
    public Set<String> scanner_scanner_getSelectedDlcSet() {
        return getScanner().scanner_getSelectedDlcSet();
    }

    @Override
    public void scanner_scanner_setSelectedDlcSet(Set<String> value) {
        getScanner().scanner_setSelectedDlcSet(value);
    }

    @Override
    protected Stage newStage() {
        HomeStage stage = new HomeStage(this);

        scannerComponentReference = new WeakReference<>(stage.scannerComponent);
        scannerSafeGlassComponentReference = new WeakReference<>(stage.scannerSafeGlassComponent);
        scannerDjNameInputComponentReference =
                new WeakReference<>(stage.scannerDjNameInputComponent);

        stage.setOnShown(event -> {
            if (!getPresenter().onViewShow_setup()) {
                return;
            }

            getPresenter().onViewShow_loadRecord();
        });

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
        }
    }
}
