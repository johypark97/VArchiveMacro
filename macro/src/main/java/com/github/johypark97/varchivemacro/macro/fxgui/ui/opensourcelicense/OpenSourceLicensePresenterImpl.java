package com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense;

import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.opensourcelicense.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.repository.OpenSourceLicenseRepository;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;

public class OpenSourceLicensePresenterImpl implements OpenSourceLicensePresenter {
    private final OpenSourceLicenseRepository openSourceLicenseRepository;

    @MvpView
    public OpenSourceLicenseView view;

    public OpenSourceLicensePresenterImpl(OpenSourceLicenseRepository openSourceLicenseRepository) {
        this.openSourceLicenseRepository = openSourceLicenseRepository;
    }

    @Override
    public void onStartView() {
        List<String> libraryList = openSourceLicenseRepository.getLibraryList();
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
            licenseText = openSourceLicenseRepository.getLicenseText(library);
        } catch (IOException e) {
            licenseText = "Resource IO Error";
        }

        view.setLicenseText(licenseText);
        view.setLicenseUrl(openSourceLicenseRepository.getLibraryUrl(library));
    }
}
