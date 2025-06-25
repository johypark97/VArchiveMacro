package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseService;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseStorageService;
import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import com.github.johypark97.varchivemacro.macro.integration.app.service.WebBrowserService;
import java.io.IOException;

public class OpenSourceLicensePresenterImpl
        implements OpenSourceLicense.OpenSourceLicensePresenter {
    private final OpenSourceLicenseService openSourceLicenseService;
    private final OpenSourceLicenseStorageService openSourceLicenseStorageService;
    private final WebBrowserService webBrowserService;

    @MvpView
    public OpenSourceLicense.OpenSourceLicenseView view;

    public OpenSourceLicensePresenterImpl(OpenSourceLicenseService openSourceLicenseService,
            OpenSourceLicenseStorageService openSourceLicenseStorageService,
            WebBrowserService webBrowserService) {
        this.openSourceLicenseService = openSourceLicenseService;
        this.openSourceLicenseStorageService = openSourceLicenseStorageService;
        this.webBrowserService = webBrowserService;
    }

    private void loadLicenseList() {
        try {
            openSourceLicenseStorageService.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startView() {
        loadLicenseList();

        view.showLibraryList(openSourceLicenseService.findAllLibrary());
    }

    @Override
    public void showLicense(String value) {
        License license = openSourceLicenseService.findLicense(value);

        view.showLicenseText(license.licenseText());
        view.showLibraryUrl(license.libraryUrl());
    }

    @Override
    public void openWebBrowser(String url) {
        webBrowserService.open(url);
    }
}
