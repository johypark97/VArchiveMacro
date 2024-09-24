package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoader.ScannerLoaderPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.ScannerLoader.ScannerLoaderView;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.component.ScannerDjNameInputComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.scannerloader.component.ScannerSafeGlassComponent;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import javafx.scene.layout.StackPane;

public class ScannerLoaderViewImpl extends StackPane implements ScannerLoaderView {
    private final ScannerDjNameInputComponent scannerDjNameInputComponent =
            new ScannerDjNameInputComponent();
    private final ScannerSafeGlassComponent scannerSafeGlassComponent =
            new ScannerSafeGlassComponent();

    @MvpPresenter
    public ScannerLoaderPresenter presenter;

    public ScannerLoaderViewImpl() {
        scannerDjNameInputComponent.onLoad = () -> presenter.loadRemoteRecord();

        super.getChildren().add(scannerDjNameInputComponent);
        super.getChildren().add(scannerSafeGlassComponent);
    }

    @Override
    public void startView() {
        presenter.onStartView();
    }

    @Override
    public String getDjNameText() {
        return scannerDjNameInputComponent.getDjNameText();
    }

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
    public void showDjNameInputError(String message) {
        scannerDjNameInputComponent.showError(message);
    }

    @Override
    public void hideDjNameInputError() {
        scannerDjNameInputComponent.hideError();
    }
}
