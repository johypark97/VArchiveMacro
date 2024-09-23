package com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense;

import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicenseView;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;

public class OpenSourceLicensePresenterImpl implements OpenSourceLicensePresenter {
    private final LicenseModel licenseModel;

    @MvpView
    public OpenSourceLicenseView view;

    public OpenSourceLicensePresenterImpl(LicenseModel licenseModel) {
        this.licenseModel = licenseModel;
    }

    @Override
    public void onStartView() {
        List<String> libraryList = licenseModel.getLibraryList();
        view.setLibraryList(FXCollections.observableList(libraryList));
    }

    @Override
    public void onStopView() {
        view.getWindow().hide();
    }

    @Override
    public void showLicense(String library) {
        String licenseText;
        try {
            licenseText = licenseModel.getLicenseText(library);
        } catch (IOException e) {
            licenseText = "Resource IO Error";
        }

        view.setLicenseText(licenseText);
        view.setLicenseUrl(licenseModel.getLibraryUrl(library));
    }
}
