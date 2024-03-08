package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.HomeStage;
import java.lang.ref.WeakReference;
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
    public ScannerSetupView getScannerSetupView() {
        return new ScannerSetupView() {
            @Override
            public void showForbiddenMark() {
                getScannerSafeGlass().showForbiddenMark();
                getScannerSafeGlass().setText("Failed to load database. Not available.");
                getScannerSafeGlass().setVisible(true);
                getScannerSafeGlass().requestFocus();
            }

            @Override
            public void showLoadingMark(String djName) {
                String message =
                        String.format("Loading records from the server.\nDJ Name: %s", djName);

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
                getScanner().setVisible(true);
                getScanner().requestFocus();
            }
        };
    }

    @Override
    public void scanner_setup_loadRemoteRecord(String djName) {
        getPresenter().scanner_setup_onLoadRemoteRecord(djName);
    }

    @Override
    protected Stage newStage() {
        HomeStage stage = new HomeStage(this);

        scannerComponentReference = new WeakReference<>(stage.scannerComponent);
        scannerSafeGlassComponentReference = new WeakReference<>(stage.scannerSafeGlassComponent);
        scannerDjNameInputComponentReference =
                new WeakReference<>(stage.scannerDjNameInputComponent);

        stage.setOnShown(event -> getPresenter().onViewShow());

        return stage;
    }
}
