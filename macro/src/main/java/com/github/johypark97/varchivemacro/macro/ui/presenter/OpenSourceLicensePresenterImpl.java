package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.application.license.model.License;
import com.github.johypark97.varchivemacro.macro.application.license.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.application.service.WebBrowserService;
import com.github.johypark97.varchivemacro.macro.infrastructure.license.loader.OpenSourceLicenseLoader;
import java.io.IOException;
import java.util.List;

public class OpenSourceLicensePresenterImpl
        implements OpenSourceLicense.OpenSourceLicensePresenter {
    private final OpenSourceLicenseLoader openSourceLicenseLoader;
    private final OpenSourceLicenseRepository openSourceLicenseRepository;
    private final WebBrowserService webBrowserService;

    @MvpView
    public OpenSourceLicense.OpenSourceLicenseView view;

    public OpenSourceLicensePresenterImpl(OpenSourceLicenseLoader openSourceLicenseLoader,
            OpenSourceLicenseRepository openSourceLicenseRepository,
            WebBrowserService webBrowserService) {
        this.openSourceLicenseLoader = openSourceLicenseLoader;
        this.openSourceLicenseRepository = openSourceLicenseRepository;
        this.webBrowserService = webBrowserService;
    }

    private void loadLicenseList() {
        List<License> licenseList;
        try {
            licenseList = openSourceLicenseLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        licenseList.forEach(openSourceLicenseRepository::save);
    }

    @Override
    public void startView() {
        loadLicenseList();

        view.showLibraryList(openSourceLicenseRepository.findAllLibrary());
    }

    @Override
    public void showLicense(String value) {
        License license = openSourceLicenseRepository.findLicense(value);

        view.showLicenseText(license.licenseText());
        view.showLibraryUrl(license.libraryUrl());
    }

    @Override
    public void openWebBrowser(String url) {
        webBrowserService.open(url);
    }
}
