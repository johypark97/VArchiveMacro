package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.OpenSourceLicenseContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.OpenSourceLicense;
import java.io.IOException;

public class OpenSourceLicensePresenterImpl implements OpenSourceLicense.Presenter {
    private final GlobalContext globalContext;
    private final OpenSourceLicenseContext openSourceLicenseContext;

    @MvpView
    public OpenSourceLicense.View view;

    public OpenSourceLicensePresenterImpl(GlobalContext globalContext,
            OpenSourceLicenseContext openSourceLicenseContext) {
        this.globalContext = globalContext;
        this.openSourceLicenseContext = openSourceLicenseContext;
    }

    private void loadLicenseList() {
        try {
            openSourceLicenseContext.openSourceLicenseStorageService.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startView() {
        loadLicenseList();

        view.showLibraryList(openSourceLicenseContext.openSourceLicenseService.findAllLibrary());
    }

    @Override
    public void showLicense(String value) {
        License license = openSourceLicenseContext.openSourceLicenseService.findLicense(value);

        view.showLicenseText(license.licenseText());
        view.showLibraryUrl(license.libraryUrl());
    }

    @Override
    public void openWebBrowser(String url) {
        globalContext.webBrowserService.open(url);
    }
}
